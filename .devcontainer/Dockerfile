# syntax=docker/dockerfile:1

FROM rocm/dev-ubuntu-22.04:7.1.1 AS rocm-llvm

FROM ubuntu:22.04 AS ifcos-build

ARG IFCOPENSHELL_GIT_TAG=eafa158ca0cd5ba2ca22b5e588b0375cab2efbce

COPY --from=rocm-llvm /opt/rocm-7.1.1/ /opt/rocm/

RUN apt-get update && apt-get install -y --no-install-recommends \
        ca-certificates git cmake ninja-build \
        libboost-all-dev \
        libocct-foundation-dev libocct-modeling-algorithms-dev libocct-modeling-data-dev \
        libocct-ocaf-dev libocct-visualization-dev libocct-data-exchange-dev \
        libhdf5-dev libeigen3-dev libgmp-dev libmpfr-dev libxml2-dev \
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

FROM mcr.microsoft.com/devcontainers/java:1-21-bookworm

USER root

COPY --from=rocm-llvm /opt/rocm-7.1.1/ /opt/rocm/
COPY --from=ifcos-build /opt/ifcopenshell /opt/ifcopenshell

RUN apt-get update && apt-get install -y --no-install-recommends \
    xfonts-scalable libocct-data-exchange-dev libocct-draw-dev \
    libocct-foundation-dev libocct-modeling-algorithms-dev libocct-modeling-data-dev libocct-ocaf-dev \
    libocct-visualization-dev libboost-all-dev libhdf5-dev libmpfr-dev libgmp3-dev libxml2-dev \
    cmake ninja-build ccache libeigen3-dev make \
    && ln -sf /opt/rocm/llvm/bin/clang /usr/local/bin/clang \
    && ln -sf /opt/rocm/llvm/bin/clang++ /usr/local/bin/clang++ \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN bash -c "source /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven"

RUN git clone https://github.com/RUB-Informatik-im-Bauwesen/OpenBimRL.git api
RUN cd api && git checkout 83bd65f && mvn -Dproject.build.sourceEncoding=ISO-8859-1 compiler:compile jar:jar install:install -DgroupId=inf.bi.rub.de -DartifactId=OpenBIMRL-API -Dversion=2023.07.1 -Dpackaging=jar
RUN rm -rf api

ENV ROCM_PATH=/opt/rocm
ENV PATH=${ROCM_PATH}/llvm/bin:${PATH}
ENV CC=${ROCM_PATH}/llvm/bin/clang
ENV CXX=${ROCM_PATH}/llvm/bin/clang++
ENV OPENBIMRL_USE_PREBUILT_IFCOPENSHELL=ON
ENV OPENBIMRL_IFCOPENSHELL_PREFIX=/opt/ifcopenshell
