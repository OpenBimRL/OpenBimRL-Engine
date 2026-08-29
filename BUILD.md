# Bazel build (primary)

OpenBimRL-Engine builds with **Bazel** (Bzlmod). The Kotlin/JVM sources and the JNI
native library are produced as classpath resource `libopenbimrl_jni.so`.

## Prerequisites (DevContainer)

- Bazelisk (`bazel`) — installed in the DevContainer `Dockerfile.dev`
- Prebuilt IfcOpenShell at `/opt/ifcopenshell` (`OPENBIMRL_IFCOPENSHELL_PREFIX`)
- System LLVM clang (`/usr/bin/clang`, `/usr/bin/clang++`) + libomp — default for
  host tools and native cmake (same toolchain as `llvm.dockerfile`)
- Optional ROCm LLVM at `/opt/rocm/llvm` — required only for `--config=rocm_offload`
- Optional CUDA toolkit (`nvcc` / CUDA LLVM) — scaffolding for `--config=cuda_offload`
  (NVIDIA offload is WIP; see below)
- OpenCASCADE / Eigen / OpenMP packages (already in the image)
- Java **21** runtime (local `JAVA_HOME` on host/CI; Bazel `--java_runtime_version=21`
  downloads `remotejdk_21` unless `--config=docker` in the Engine dockerfiles).
  Sources still compile to Java 17 bytecode.

## Commands

From `OpenBimRL-Engine/`:

```bash
# Native cmake (rules_foreign_cc) + Kotlin/JVM library with embedded JNI .so
bazel build //:engine_lib

# Console entrypoint
bazel build //:console

# Fat jar for Docker / `java -jar` (Main-Class + embedded JNI .so)
bazel build //:console_app_deploy.jar

# Fast JVM-only tests (no IFC execute paths required)
bazel test //:engine_unit_tests

# Full Engine Surefire-equivalent suite (loads libopenbimrl_jni.so via JNI).
# Includes parallel rails/walls IFC checks; pathfinding integration stays manual.
bazel test //:engine_tests

# Standalone parallel IFC checks (also covered by //:engine_tests):
bazel test //:parallel_checks_test

# Manual opt-in pathfinding IFC integration (hangs under investigation — RFC-2):
bazel test //:pathfinding_minimal_ifc_test
bazel test //:show_distances_test
bazel test //:pathfinding_movement_cost_test
```

`bazel test //...` from Engine is **JVM-focused** (e.g. `engine_unit_tests`,
`engine_tests`). Native C++ unit tests live in the `openbimrl_native` module —
run them from `src/main/cpp/` (see that directory’s README):

```bash
cd src/main/cpp
bazel test //:edge_costs_test
# or: bazel test //:native_unit_tests
# or: bazel test //...
```

Native CI (OpenBimRL-Engine-Native
[`.github/workflows/bazel-test.yml`](src/main/cpp/.github/workflows/bazel-test.yml))
runs Bazel unit tests only. IFC integration gtests (`OpenBIMRL_Native_Test`)
stay on local CMake/`ctest` with `OPENBIMRL_BUILD_NATIVE_TESTS=ON` (same README).

Useful flags:

```bash
bazel test //:engine_tests --test_output=all
bazel build //:native_lib_so      # JNI resource only
bazel build //:openbimrl_native  # alias → @openbimrl_native//:openbimrl_native
```

### ROCm OpenMP GPU offload (opt-in)

Default builds stay CPU-only OpenMP with system clang + libomp. To compile device
offload code with ROCm LLVM:

```bash
# Arch required for docker / headless hosts; local GPUs can omit and use rocminfo.
OPENBIMRL_ROCM_OFFLOAD_ARCH=gfx1100 \
  bazel build //:native_lib_so --config=rocm_offload
```

Equivalent without the named config: `--define OPENBIMRL_ENABLE_ROCM_OFFLOAD=ON`.

### CUDA OpenMP GPU offload (opt-in, WIP)

NVIDIA path mirrors ROCm. Host compile uses system clang; CUDA toolkit (`nvcc` /
[CUDA LLVM](https://developer.nvidia.com/cuda-llvm-compiler)) must be on the image.
Device offload flags are wired in CMake but may still need a CUDA-capable clang
and further cmake work.

```bash
OPENBIMRL_CUDA_OFFLOAD_ARCH=sm_89 \
  bazel build //:native_lib_so --config=cuda_offload
```

| Knob | Role |
|------|------|
| `--config=rocm_offload` | Native cmake → `/opt/rocm/llvm/bin/clang++` + `OPENBIMRL_ENABLE_ROCM_OFFLOAD=ON` |
| `--config=cuda_offload` | Native cmake → `/usr/bin/clang++` + `OPENBIMRL_ENABLE_CUDA_OFFLOAD=ON` (WIP) |
| `OPENBIMRL_ROCM_OFFLOAD_ARCH` | Target `gfx*` (`.bazelrc` `action_env`); empty → CMake runs `rocminfo` |
| `OPENBIMRL_CUDA_OFFLOAD_ARCH` | Target `sm_*` (optional; empty → CMake `nvidia-smi`) |
| Host `CC`/`CXX` in `.bazelrc` | `/usr/bin/clang` / `clang++` (libomp); offload configs override native cmake only |

```bash
OPENBIMRL_ROCM_OFFLOAD_ARCH=gfx1100 bazel build //:engine_lib --config=rocm_offload
```

## Native library notes

- Native is a nested Bazel module (`openbimrl_native`) at git submodule
  `src/main/cpp`, wired with `bazel_dep` + `local_path_override` in Engine
  `MODULE.bazel`. It owns `rules_foreign_cc` cmake + `@nlohmann_json_src`.
- `//:openbimrl_native` aliases `@openbimrl_native//:openbimrl_native`
  (`OPENBIMRL_USE_PREBUILT_IFCOPENSHELL=ON`).
- `//:native_lib_so` renames/copies that artifact to classpath resource
  `libopenbimrl_jni.so` (Engine version stays on the Maven GAV /
  `OPENBIMRL_ENGINE_VERSION`, not in the resource filename).
- JVM loads it via `NativeEngine.loadNative()` (`System.loadLibrary("openbimrl_jni")`
  with classpath extraction fallback).
- `openbimrl_jni` links `-static-libstdc++ -static-libgcc` by default
  (`OPENBIMRL_STATIC_LIBSTDCXX=ON`): C++ runtime symbols ship inside the JNI
  `.so` so published jars are not tied to the host `libstdc++.so.6` version.
  IfcOpenShell / OCCT remain dynamic (Engine runtime image / `LD_LIBRARY_PATH`).
  Does not remove the need for a compatible **glibc** or **libomp** on the host.
- Default compilers are `/usr/bin/clang` and `/usr/bin/clang++` in the Native
  module BUILD (`generate_crosstool_file = False`); `--config=rocm_offload`
  selects `/opt/rocm/llvm`; `--config=cuda_offload` keeps host clang + CUDA env.

## Private / non-Central JVM deps

`OpenBimRL schema` (`@openbimrl_api_src`) and `BVH` are built from pinned GitHub
source archives in `MODULE.bazel` via Bzlmod `use_repo_rule` + `http_archive`
(`@openbimrl_api_src`, `@bvh_src`). Overlay BUILD files live under `third_party/`.
All other deps use `rules_jvm_external`. Those private jars are **bundled** into
the published Maven artifact (they are not published as separate packages).

## Publish to GitHub Packages (Maven)

Coordinates: `de.rub.bi.inf.openbimrl.engine:core:<version>`  
Repo URL: `https://maven.pkg.github.com/OpenBimRL/OpenBimRL-Engine`

Uses `kt_jvm_export` + Bazel `//:openbimrl_engine_maven.publish`
(`rules_jvm_external` 7.1+ MavenPublisher; requires `rules_java` 9 / Bazel 9).

Default version is `2026.08.23` (`.bazelrc` / `MODULE.bazel`). CI uses
`YYYY.MM.DD` via `--define OPENBIMRL_ENGINE_VERSION=...`.

```bash
# Build the export jar + generated POM (no upload)
bazel build //:openbimrl_engine_maven

# Dry-run: publish to a local directory (no token)
MAVEN_REPO=file:///tmp/openbimrl-m2 \
  bazel run //:openbimrl_engine_maven.publish

# Publish to GitHub Packages (needs packages:write token)
MAVEN_REPO=https://maven.pkg.github.com/OpenBimRL/OpenBimRL-Engine \
MAVEN_USER=your-user \
MAVEN_PASSWORD=ghp_... \
  bazel run //:openbimrl_engine_maven.publish \
    --define OPENBIMRL_ENGINE_VERSION="$(date -u +%Y.%m.%d)"
```

### CI workflow (`.github/workflows/maven-publish.yml`)

On push to `main` / `master`:

1. Install apt deps (clang + libomp) + JDK 21 + Bazelisk (no Maven CLI)
2. Build IfcOpenShell with clang into `$RUNNER_TEMP/ifcopenshell`, copy to `/opt/ifcopenshell`
3. `bazel test //:engine_unit_tests //:engine_tests`
4. `bazel run //:openbimrl_engine_maven.publish --define OPENBIMRL_ENGINE_VERSION=YYYY.MM.DD`  
   with `MAVEN_REPO` / `MAVEN_USER` / `MAVEN_PASSWORD` (`GITHUB_TOKEN`)

**Permissions:** `contents: read`, `packages: write` (uses `GITHUB_TOKEN`).

OpenBimRL-Engine-REST is built with Bazel in its own repository and consumes
the published GitHub Packages artifact `de.rub.bi.inf.openbimrl.engine:core`
(and the Engine Docker image for native IfcOpenShell / OCCT).

## Docker images

Multi-stage images under `OpenBimRL-Engine/` (IfcOpenShell → Bazel → JRE).
Requires the Native submodule (`src/main/cpp`). linux/amd64 only.

| File | Role |
|------|------|
| `llvm.dockerfile` | Default CPU image — system clang + libomp, no ROCm/CUDA |
| `rocm.dockerfile` | AMD GPU — JVM at build time; native `.so` at container start (ROCm offload) |
| `nvcc.dockerfile` | NVIDIA — JVM at build time; native `.so` at container start (CUDA offload, WIP) |

```bash
# CPU / default
docker build -f llvm.dockerfile -t openbimrl-engine:llvm .

# AMD offload — native compiled at container start; set arch at run time
docker build -f rocm.dockerfile -t openbimrl-engine:rocm .
docker run --device=/dev/kfd --device=/dev/dri --group-add video \
  -e OPENBIMRL_ROCM_OFFLOAD_ARCH=gfx1100 \
  openbimrl-engine:rocm …

# NVIDIA — native compiled at container start; set arch at run time (WIP)
docker build -f nvcc.dockerfile -t openbimrl-engine:nvcc .
docker run --gpus all \
  -e OPENBIMRL_CUDA_OFFLOAD_ARCH=sm_89 \
  openbimrl-engine:nvcc …
```

ghcr.io publish (`.github/workflows/build_and_test.yaml`) builds all three images
on push to `main`/`master` (linux/amd64):

| Tag | Dockerfile |
|-----|------------|
| `:latest`, `:llvm`, `:<YYYY.MM.DD>`, `:<YYYY.MM.DD>-llvm` | `llvm.dockerfile` |
| `:rocm`, `:<YYYY.MM.DD>-rocm` | `rocm.dockerfile` |
| `:nvcc`, `:<YYYY.MM.DD>-nvcc` | `nvcc.dockerfile` |

The DevContainer `Dockerfile.dev` uses Ubuntu 24.04 (matches ROCm LLVM glibc),
system clang for Bazel defaults, and `--config=rocm_offload` when needed. Java 21
is installed via `.devcontainer/devcontainer.json` features.

## JNI / jni-bind (Track B)

JVM↔native interop uses `NativeEngine` (`external` methods) and `libopenbimrl_jni.so`
built with [jni-bind](https://github.com/google/jni-bind) Release-1.5.1 via Bazel
`http_archive` (`@jni-bind//:jni_bind`, `#include "jni_bind.h"`). Opaque IFC handles
are `jlong` / `IfcPointer.handle`.
