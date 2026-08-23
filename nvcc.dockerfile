# syntax=docker/dockerfile:1

# NVIDIA Engine image: CUDA toolkit (nvcc / CUDA LLVM) + host clang.
# Mirrors rocm.dockerfile structure. Full OpenMP GPU offload for NVIDIA is WIP —
# CMake/--config=cuda_offload hooks exist; device codegen may still need a
# CUDA-capable clang and further cmake work. See BUILD.md.
#
#   docker build -f nvcc.dockerfile -t openbimrl-engine:nvcc .
#   docker build -f nvcc.dockerfile -t openbimrl-engine:nvcc \
#     --build-arg ENABLE_CUDA_OFFLOAD=ON .
#   # Optional arch (else CMake tries nvidia-smi, same idea as ROCm/rocminfo):
#   docker build -f nvcc.dockerfile -t openbimrl-engine:nvcc \
#     --build-arg ENABLE_CUDA_OFFLOAD=ON --build-arg CUDA_OFFLOAD_ARCH=sm_89 .
#
# Supported platform: linux/amd64 only.
# CUDA LLVM / nvcc: https://developer.nvidia.com/cuda-llvm-compiler

ARG IFCOPENSHELL_GIT_TAG=eafa158ca0cd5ba2ca22b5e588b0375cab2efbce
ARG BAZELISK_VERSION=1.29.0
ARG CUDA_OFFLOAD_ARCH=
# Default OFF so the image builds with host clang + CUDA toolkit present.
# Set ON to exercise --config=cuda_offload (WIP). CUDA_OFFLOAD_ARCH is optional.
ARG ENABLE_CUDA_OFFLOAD=OFF

FROM nvidia/cuda:12.6.3-devel-ubuntu24.04 AS cuda-toolkit

FROM nvidia/cuda:12.6.3-devel-ubuntu24.04 AS ifcos-build

ARG IFCOPENSHELL_GIT_TAG

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=UTC

RUN apt-get update && apt-get install -y --no-install-recommends \
        ca-certificates git cmake ninja-build \
        clang libomp-dev \
        libboost-all-dev \
        libocct-foundation-dev libocct-modeling-algorithms-dev libocct-modeling-data-dev \
        libocct-ocaf-dev libocct-visualization-dev libocct-data-exchange-dev \
        libhdf5-dev libeigen3-dev libgmp-dev libmpfr-dev libxml2-dev \
        libgl1-mesa-dev libx11-dev libxext-dev libxi-dev libxmu-dev libxt-dev \
        libtbb-dev tcl-dev tk-dev occt-misc \
    && rm -rf /var/lib/apt/lists/*

RUN git clone https://github.com/IfcOpenShell/IfcOpenShell.git /src \
    && cd /src \
    && git checkout "${IFCOPENSHELL_GIT_TAG}"

# Host build with system clang (CUDA nvcc remains available for offload hooks).
RUN cmake -G Ninja -S /src/cmake -B /build \
        -DCMAKE_C_COMPILER=/usr/bin/clang \
        -DCMAKE_CXX_COMPILER=/usr/bin/clang++ \
        -DCMAKE_INSTALL_PREFIX=/opt/ifcopenshell \
        -DCMAKE_BUILD_TYPE=Release \
        -DOCC_INCLUDE_DIR=/usr/include/opencascade \
        -DOCC_LIBRARY_DIR=/usr/lib/x86_64-linux-gnu \
        -DBUILD_SHARED_LIBS=ON \
        -DSCHEMA_VERSIONS="2x3;4;4x3_add2" \
        -DBUILD_CONVERT=OFF \
        -DBUILD_IFCPYTHON=OFF \
        -DBUILD_GEOMSERVER=OFF \
        -DBUILD_EXAMPLES=OFF \
        -DBUILD_DOCUMENTATION=OFF \
        -DWITH_CGAL=OFF \
        -DCOLLADA_SUPPORT=OFF \
        -DHDF5_SUPPORT=OFF \
        -DGLTF_SUPPORT=OFF \
        -DIFCXML_SUPPORT=OFF \
        -DUSD_SUPPORT=OFF \
    && cmake --build /build -j"$(nproc)" \
    && cmake --install /build

# ---------------------------------------------------------------------------
# Bazel build. Optional --config=cuda_offload (WIP NVIDIA OpenMP offload).
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-noble AS engine-build

ARG BAZELISK_VERSION
ARG CUDA_OFFLOAD_ARCH
ARG ENABLE_CUDA_OFFLOAD

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=UTC
ENV OPENBIMRL_USE_PREBUILT_IFCOPENSHELL=ON
ENV OPENBIMRL_IFCOPENSHELL_PREFIX=/opt/ifcopenshell
ENV OPENBIMRL_ENABLE_CUDA_OFFLOAD=${ENABLE_CUDA_OFFLOAD}
ENV OPENBIMRL_CUDA_OFFLOAD_ARCH=${CUDA_OFFLOAD_ARCH}
ENV CUDA_HOME=/usr/local/cuda
ENV PATH=${CUDA_HOME}/bin:${PATH}

COPY --from=ifcos-build /opt/ifcopenshell /opt/ifcopenshell
COPY --from=cuda-toolkit /usr/local/cuda /usr/local/cuda

RUN apt-get update && apt-get install -y --no-install-recommends \
        ca-certificates curl git \
        clang libomp-dev libomp5 \
        make cmake ninja-build \
        libeigen3-dev \
        xfonts-scalable \
        libocct-data-exchange-dev libocct-draw-dev libocct-foundation-dev \
        libocct-modeling-algorithms-dev libocct-modeling-data-dev \
        libocct-ocaf-dev libocct-visualization-dev \
        libmpfr-dev libboost-all-dev libhdf5-dev libgmp-dev libxml2-dev \
    && curl -fsSL -o /usr/local/bin/bazelisk \
         "https://github.com/bazelbuild/bazelisk/releases/download/v${BAZELISK_VERSION}/bazelisk-linux-amd64" \
    && chmod +x /usr/local/bin/bazelisk \
    && ln -sf /usr/local/bin/bazelisk /usr/local/bin/bazel \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /src
COPY . .

RUN test -f src/main/cpp/MODULE.bazel \
    || { echo "ERROR: src/main/cpp submodule missing; checkout Native before docker build" >&2; exit 1; }

RUN set -eux; \
    BAZEL_ARGS=(build //:console_app_deploy.jar); \
    if [ "${ENABLE_CUDA_OFFLOAD}" = "ON" ]; then \
      if [ ! -x /usr/local/cuda/bin/nvcc ]; then \
        echo "ERROR: ENABLE_CUDA_OFFLOAD=ON but /usr/local/cuda/bin/nvcc is missing." >&2; \
        exit 1; \
      fi; \
      BAZEL_ARGS+=(--config=cuda_offload); \
    fi; \
    bazel "${BAZEL_ARGS[@]}" \
    && mkdir -p /out \
    && cp -f bazel-bin/console_app_deploy.jar /out/app.jar

FROM eclipse-temurin:21-jre-noble

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=UTC
ENV LD_LIBRARY_PATH=/opt/ifcopenshell/lib

COPY --from=ifcos-build /opt/ifcopenshell /opt/ifcopenshell
COPY --from=engine-build /out/app.jar /app/app.jar

RUN apt-get update && apt-get install -y --no-install-recommends \
        xfonts-scalable \
        libocct-data-exchange-dev libocct-draw-dev libocct-foundation-dev \
        libocct-modeling-algorithms-dev libocct-modeling-data-dev \
        libocct-ocaf-dev libocct-visualization-dev \
        libboost-all-dev libmpfr-dev libgmp-dev libxml2-dev libhdf5-dev \
        libomp5 \
    && cp -a /opt/ifcopenshell/lib/libIfcGeom.so* /usr/lib/ \
    && cp -a /opt/ifcopenshell/lib/libIfcParse.so* /usr/lib/ \
    && ldconfig \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
CMD ["java", "-jar", "app.jar"]
