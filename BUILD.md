# Bazel build (primary)

OpenBimRL-Engine builds with **Bazel** (Bzlmod). The Kotlin/JVM sources and the JNA
C-ABI native library are produced as classpath resource
`libOpenBimRL-Engine-Native-x86_64.so` (no JNI / jni-bind).

## Prerequisites (DevContainer)

- Bazelisk (`bazel`) — installed in monorepo `Dockerfile.dev`
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
# Native cmake (rules_foreign_cc) + Kotlin/JVM library with embedded native .so
bazel build //:engine_lib

# Console entrypoint
bazel build //:console

# Fat jar for Docker / `java -jar` (Main-Class + embedded native .so)
bazel build //:console_app_deploy.jar

# Fast JVM-only tests (no IFC execute paths required)
bazel test //:engine_unit_tests

# Full Engine Surefire-equivalent suite (loads native .so via JNA)
bazel test //:engine_tests
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
bazel build //:native_lib_so      # JNA resource only
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

Monorepo helper: `./scripts/dev-start.sh --gpu` / `--gpu-arch gfx1100` passes the ROCm config.

## Native library notes

- Native is a nested Bazel module (`openbimrl_native`) at git submodule
  `src/main/cpp`, wired with `bazel_dep` + `local_path_override` in Engine
  `MODULE.bazel`. It owns `rules_foreign_cc` cmake + `@nlohmann_json_src`.
- `//:openbimrl_native` aliases `@openbimrl_native//:openbimrl_native`
  (`OPENBIMRL_USE_PREBUILT_IFCOPENSHELL=ON`).
- `//:native_lib_so` renames/copies that artifact to classpath resource
  `libOpenBimRL-Engine-Native-x86_64.so` (stable name; arch = amd64/x86_64 only.
  Engine version stays on the Maven GAV / `OPENBIMRL_ENGINE_VERSION`, not in the
  resource filename, so JNA lookup does not churn with each publish).
- JNA loads it via `FunctionsNative.create()`
  (`FunctionsNative.NATIVE_LIBRARY_RESOURCE`).
- Default compilers are `/usr/bin/clang` and `/usr/bin/clang++` in the Native
  module BUILD (`generate_crosstool_file = False`); `--config=rocm_offload`
  selects `/opt/rocm/llvm`; `--config=cuda_offload` keeps host clang + CUDA env.

## Private / non-Central JVM deps

`OpenBIMRL-API` and `BVH` are built from pinned GitHub source archives in
`MODULE.bazel` via Bzlmod `use_repo_rule` + `http_archive` (`@openbimrl_api_src`,
`@bvh_src`). Overlay BUILD files live under `third_party/`. All other deps use
`rules_jvm_external`. Those private jars are **bundled** into the published
Maven artifact (they are not published as separate packages).

## Publish to GitHub Packages (Maven)

Coordinates: `inf.bi.rub.de:openbimrl-engine:<version>`  
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

OpenBimRL-Engine-REST stays on Maven and consumes this package; it is **not**
migrated to Bazel.

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

Monorepo `Dockerfile.dev` remains the ROCm-equipped DevContainer (system clang for
Bazel defaults + ROCm on PATH for `--gpu` / `rocm_offload`).

## Out of scope (Track B)

JNI, jni-bind, rewriting `FunctionsLibrary` / `init_function` callbacks.
