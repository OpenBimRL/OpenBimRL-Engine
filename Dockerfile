# syntax=docker/dockerfile:1

ARG IFCOPENSHELL_GIT_TAG=eafa158ca0cd5ba2ca22b5e588b0375cab2efbce

FROM rocm/dev-ubuntu-22.04:7.1.1 AS rocm-llvm

FROM ubuntu:22.04 AS ifcos-build

ARG IFCOPENSHELL_GIT_TAG
ARG TARGETARCH

# Build IfcOpenShell with the same compiler family as OpenBIMRL_Native (clang, not g++).
COPY --from=rocm-llvm /opt/rocm-7.1.1/ /opt/rocm/

RUN apt-get update && apt-get install -y --no-install-recommends \
        ca-certificates git cmake ninja-build \
        libboost-all-dev \
        libocct-foundation-dev libocct-modeling-algorithms-dev libocct-modeling-data-dev \
        libocct-ocaf-dev libocct-visualization-dev libocct-data-exchange-dev \
        libhdf5-dev libeigen3-dev libgmp-dev libmpfr-dev libxml2-dev \
    && if [ "${TARGETARCH}" = "arm64" ]; then \
        apt-get install -y --no-install-recommends clang libomp-dev \
        && rm -rf /opt/rocm; \
       fi \
    && rm -rf /var/lib/apt/lists/*

RUN git clone https://github.com/IfcOpenShell/IfcOpenShell.git /src \
    && cd /src \
    && git checkout "${IFCOPENSHELL_GIT_TAG}"

RUN set -eux; \
    if [ "${TARGETARCH}" = "arm64" ]; then \
        IFCOS_CC=clang; IFCOS_CXX=clang++; \
        OCC_LIB_DIR=/usr/lib/aarch64-linux-gnu; \
    else \
        IFCOS_CC=/opt/rocm/llvm/bin/clang; IFCOS_CXX=/opt/rocm/llvm/bin/clang++; \
        OCC_LIB_DIR=/usr/lib/x86_64-linux-gnu; \
    fi; \
    cmake -G Ninja -S /src/cmake -B /build \
        -DCMAKE_C_COMPILER="${IFCOS_CC}" \
        -DCMAKE_CXX_COMPILER="${IFCOS_CXX}" \
        -DCMAKE_INSTALL_PREFIX=/opt/ifcopenshell \
        -DCMAKE_BUILD_TYPE=Release \
        -DOCC_INCLUDE_DIR=/usr/include/opencascade \
        -DOCC_LIBRARY_DIR="${OCC_LIB_DIR}" \
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

FROM maven:3.9.9-eclipse-temurin-23-noble
USER root

ARG TARGETARCH
ARG ENABLE_ROCM_OFFLOAD=OFF
ARG ROCM_OFFLOAD_ARCH=

COPY --from=rocm-llvm /opt/rocm-7.1.1/ /opt/rocm/
COPY --from=ifcos-build /opt/ifcopenshell /opt/ifcopenshell

RUN apt update && apt install -yq make cmake ninja-build ccache git libeigen3-dev \
    xfonts-scalable libocct-data-exchange-dev libocct-draw-dev libocct-foundation-dev libocct-modeling-algorithms-dev \
    libocct-modeling-data-dev libocct-ocaf-dev libocct-visualization-dev \
    libmpfr-dev libboost-all-dev libhdf5-dev libgmp-dev libxml2-dev \
    && if [ "$TARGETARCH" = "arm64" ]; then \
        apt install -yq clang libomp-dev \
        && rm -rf /opt/rocm \
        && ln -sf /usr/bin/clang /usr/local/bin/clang \
        && ln -sf /usr/bin/clang++ /usr/local/bin/clang++ ; \
    else \
        ln -sf /opt/rocm/llvm/bin/clang /usr/local/bin/clang \
        && ln -sf /opt/rocm/llvm/bin/clang++ /usr/local/bin/clang++ ; \
    fi

WORKDIR /app
RUN git clone --quiet https://github.com/RUB-Informatik-im-Bauwesen/OpenBimRL.git /build/api
RUN cd /build/api && git checkout 83bd65f && mvn -Dproject.build.sourceEncoding=ISO-8859-1 compiler:compile jar:jar install:install -DgroupId=inf.bi.rub.de -DartifactId=OpenBIMRL-API -Dversion=2023.07.1 -Dpackaging=jar --quiet

RUN git clone --quiet https://github.com/RUB-Informatik-im-Bauwesen/Maven-Bounding-Volume-Hierarchy.git /build/bvh
RUN cd /build/bvh && mvn install --quiet

COPY . /build/engine
RUN cd /build/engine && \
    if [ "$TARGETARCH" = "arm64" ]; then \
        export CC=/usr/bin/clang CXX=/usr/bin/clang++ \
            OPENBIMRL_ENABLE_ROCM_OFFLOAD=OFF OPENBIMRL_ROCM_OFFLOAD_ARCH= \
            OPENBIMRL_USE_PREBUILT_IFCOPENSHELL=ON OPENBIMRL_IFCOPENSHELL_PREFIX=/opt/ifcopenshell; \
    else \
        export CC=/opt/rocm/llvm/bin/clang CXX=/opt/rocm/llvm/bin/clang++ \
            PATH=/opt/rocm/llvm/bin:$PATH \
            OPENBIMRL_ENABLE_ROCM_OFFLOAD=${ENABLE_ROCM_OFFLOAD} OPENBIMRL_ROCM_OFFLOAD_ARCH=${ROCM_OFFLOAD_ARCH} \
            OPENBIMRL_USE_PREBUILT_IFCOPENSHELL=ON OPENBIMRL_IFCOPENSHELL_PREFIX=/opt/ifcopenshell; \
    fi && \
    mvn install -Dmaven.test.skip --quiet && mvn package -Dmaven.test.skip --quiet

RUN bash -c "cp /build/engine/target/*-jar-with-dependencies.jar app.jar"

RUN cp -a /opt/ifcopenshell/lib/libIfcGeom.so* /usr/lib/ \
    && cp -a /opt/ifcopenshell/lib/libIfcParse.so* /usr/lib/ \
    && ldconfig

RUN rm -rf /build

CMD java -jar app.jar
