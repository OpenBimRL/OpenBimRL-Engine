# syntax=docker/dockerfile:1

# Default / CPU Engine image: system LLVM clang + libomp (no ROCm / CUDA).
# Build graph: IfcOpenShell (prebuilt) → Bazel //:console_app_deploy.jar → slim JRE.
# Supported platform: linux/amd64 only.
#
#   docker build -f llvm.dockerfile -t openbimrl-engine:llvm .

ARG IFCOPENSHELL_GIT_TAG=eafa158ca0cd5ba2ca22b5e588b0375cab2efbce
ARG BAZELISK_VERSION=1.29.0

# Build IfcOpenShell on Ubuntu 24.04 / OCCT 7.6 (same generation as the runtime
# image). Building on older distros links Boost/OCCT sonames that Noble cannot load.
FROM ubuntu:24.04 AS ifcos-build

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
# Bazel build (JDK 21 + Bazelisk). Default native cmake uses system clang
# (.bazelrc / openbimrl_native BUILD).
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-noble AS engine-build

ARG BAZELISK_VERSION

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=UTC
ENV OPENBIMRL_USE_PREBUILT_IFCOPENSHELL=ON
ENV OPENBIMRL_IFCOPENSHELL_PREFIX=/opt/ifcopenshell

COPY --from=ifcos-build /opt/ifcopenshell /opt/ifcopenshell

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

RUN bazel build //:console_app_deploy.jar \
    && mkdir -p /out \
    && cp -f bazel-bin/console_app_deploy.jar /out/app.jar

# ---------------------------------------------------------------------------
# Slim runtime: JRE 21 + fat jar + IfcOpenShell / OCCT shared libs
# ---------------------------------------------------------------------------
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
