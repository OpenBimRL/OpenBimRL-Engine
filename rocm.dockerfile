# syntax=docker/dockerfile:1

# AMD GPU Engine image: ROCm LLVM/clang OpenMP offload.
#
# Build-time:  IfcOpenShell + JVM fat jar (no native .so).
# Start-time:  CMD compiles native for OPENBIMRL_ROCM_OFFLOAD_ARCH, bundles into
#              the jar, then runs java.
#
#   docker build -f rocm.dockerfile -t openbimrl-engine:rocm .
#   docker run --device=/dev/kfd --device=/dev/dri --group-add video \
#     -e OPENBIMRL_ROCM_OFFLOAD_ARCH=gfx1100 \
#     openbimrl-engine:rocm …
#
# Required at run time:
#   OPENBIMRL_ROCM_OFFLOAD_ARCH   ROCm target, e.g. gfx1100 (must match your GPU).

ARG IFCOPENSHELL_GIT_TAG=eafa158ca0cd5ba2ca22b5e588b0375cab2efbce
ARG BAZELISK_VERSION=1.29.0

FROM rocm/dev-ubuntu-24.04:7.1.1 AS ifcos-build

ARG IFCOPENSHELL_GIT_TAG

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=UTC

RUN apt-get update && apt-get install -y --no-install-recommends \
        ca-certificates git cmake ninja-build \
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
        -DCMAKE_C_COMPILER=/opt/rocm/llvm/bin/clang \
        -DCMAKE_CXX_COMPILER=/opt/rocm/llvm/bin/clang++ \
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
# JVM-only Bazel build (native compiled at container start).
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-noble AS engine-build

ARG BAZELISK_VERSION

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=UTC
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=${JAVA_HOME}/bin:${PATH}

RUN apt-get update && apt-get install -y --no-install-recommends \
        ca-certificates curl git \
    && curl -fsSL -o /usr/local/bin/bazelisk \
         "https://github.com/bazelbuild/bazelisk/releases/download/v${BAZELISK_VERSION}/bazelisk-linux-amd64" \
    && chmod +x /usr/local/bin/bazelisk \
    && ln -sf /usr/local/bin/bazelisk /usr/local/bin/bazel \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /src
COPY . .

RUN test -f src/main/cpp/MODULE.bazel \
    || { echo "ERROR: src/main/cpp submodule missing; checkout Native before docker build" >&2; exit 1; }

RUN bazel build --config=docker //:console_jvm_app_deploy.jar \
    && mkdir -p /out \
    && cp -f bazel-bin/console_jvm_app_deploy.jar /out/app-jvm.jar

# ---------------------------------------------------------------------------
# Runtime: ROCm + native toolchain; CMD compiles .so then runs JVM jar.
# ---------------------------------------------------------------------------
FROM rocm/dev-ubuntu-24.04:7.1.1

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=UTC
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=${JAVA_HOME}/bin:${PATH}
ENV OPENBIMRL_NATIVE_CACHE=/var/cache/openbimrl
ENV OPENBIMRL_NATIVE_BUILD=/var/cache/openbimrl/build
ENV OPENBIMRL_NATIVE_INSTALL=/var/cache/openbimrl/install
ENV OPENBIMRL_NATIVE_SO=/var/cache/openbimrl/install/lib/libOpenBimRL-Engine-Native-x86_64.so
ENV OPENBIMRL_NATIVE_SRC=/src/native
ENV OPENBIMRL_IFCOPENSHELL_PREFIX=/opt/ifcopenshell
ENV APP_JVM=/app/app-jvm.jar
ENV APP_RUN=/app/app.jar
ENV LD_LIBRARY_PATH=/opt/rocm/lib:/opt/ifcopenshell/lib

RUN apt-get update && apt-get install -y --no-install-recommends \
        ca-certificates cmake ninja-build zip \
        libboost-all-dev \
        libocct-foundation-dev libocct-modeling-algorithms-dev libocct-modeling-data-dev \
        libocct-ocaf-dev libocct-visualization-dev libocct-data-exchange-dev \
        libeigen3-dev libgmp-dev libmpfr-dev libxml2-dev libhdf5-dev \
        xfonts-scalable \
    && rm -rf /var/lib/apt/lists/*

RUN rm -rf ${OPENBIMRL_NATIVE_CACHE} \
    && mkdir -p ${OPENBIMRL_NATIVE_BUILD} ${OPENBIMRL_NATIVE_INSTALL}/lib

COPY --from=engine-build /opt/java/openjdk /opt/java/openjdk
COPY --from=ifcos-build /opt/ifcopenshell /opt/ifcopenshell

RUN cp -a /opt/ifcopenshell/lib/libIfcGeom.so* /usr/lib/ \
    && cp -a /opt/ifcopenshell/lib/libIfcParse.so* /usr/lib/ \
    && ldconfig

COPY --from=engine-build /out/app-jvm.jar /app/app-jvm.jar
COPY src/main/cpp /src/native

WORKDIR /app
ENTRYPOINT ["/bin/bash", "-c", "\
set -e; \
cmake -G Ninja -S \"${OPENBIMRL_NATIVE_SRC}\" -B \"${OPENBIMRL_NATIVE_BUILD}\" \
    -DCMAKE_C_COMPILER=/opt/rocm/llvm/bin/clang \
    -DCMAKE_CXX_COMPILER=/opt/rocm/llvm/bin/clang++ \
    -DCMAKE_INSTALL_PREFIX=\"${OPENBIMRL_NATIVE_INSTALL}\" \
    -DCMAKE_BUILD_TYPE=Release \
    -DOPENBIMRL_ENABLE_ROCM_OFFLOAD=ON \
    -DOPENBIMRL_ROCM_OFFLOAD_ARCH=\"${OPENBIMRL_ROCM_OFFLOAD_ARCH}\" \
    -DOPENBIMRL_IFCOPENSHELL_PREFIX=\"${OPENBIMRL_IFCOPENSHELL_PREFIX}\" \
    -DOPENBIMRL_USE_PREBUILT_IFCOPENSHELL=ON \
    -DOPENBIMRL_BUILD_NATIVE_TESTS=OFF \
&& cmake --build \"${OPENBIMRL_NATIVE_BUILD}\" -j\"$(nproc)\" \
&& cmake --install \"${OPENBIMRL_NATIVE_BUILD}\" \
&& cp -f \"${OPENBIMRL_NATIVE_INSTALL}/lib/libOpenBIMRL_Native.so\" \"${OPENBIMRL_NATIVE_SO}\" \
&& cp -f \"${APP_JVM}\" \"${APP_RUN}\" \
&& cd \"$(dirname \"${OPENBIMRL_NATIVE_SO}\")\" && zip -q -j \"${APP_RUN}\" \"$(basename \"${OPENBIMRL_NATIVE_SO}\")\" \
&& exec java -jar \"${APP_RUN}\" \"$@\"", "--"]
CMD []
