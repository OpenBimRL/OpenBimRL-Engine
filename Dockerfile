# syntax=docker/dockerfile:1
FROM rocm/dev-ubuntu-22.04:7.1.1 AS rocm-llvm
FROM maven:3.9.9-eclipse-temurin-23-noble
USER root

ARG TARGETARCH
ARG ENABLE_ROCM_OFFLOAD=OFF
ARG ROCM_OFFLOAD_ARCH=

COPY --from=rocm-llvm /opt/rocm-7.1.1/ /opt/rocm/

RUN apt update && apt install -yq make cmake git \
    # lib occt:
    xfonts-scalable libocct-data-exchange-dev libocct-draw-dev libocct-foundation-dev libocct-modeling-algorithms-dev \
    libocct-modeling-data-dev libocct-ocaf-dev libocct-visualization-dev \
    # other dependencies:
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
        export CC=/usr/bin/clang CXX=/usr/bin/clang++ OPENBIMRL_ENABLE_ROCM_OFFLOAD=OFF OPENBIMRL_ROCM_OFFLOAD_ARCH=; \
    else \
        export CC=/opt/rocm/llvm/bin/clang CXX=/opt/rocm/llvm/bin/clang++ \
            PATH=/opt/rocm/llvm/bin:$PATH \
            OPENBIMRL_ENABLE_ROCM_OFFLOAD=${ENABLE_ROCM_OFFLOAD} OPENBIMRL_ROCM_OFFLOAD_ARCH=${ROCM_OFFLOAD_ARCH}; \
    fi && \
    mvn install -Dmaven.test.skip --quiet && mvn package -Dmaven.test.skip --quiet

RUN bash -c "cp /build/engine/target/*-jar-with-dependencies.jar app.jar"
RUN mv /build/engine/build/cmake/_deps/ifcopenshell-build/libIfcGeom.so.0.7.0 /usr/lib/
RUN ln -s /usr/lib/libIfcGeom.so.0.7.0 /usr/lib/libIfcGeom.so.0.7
RUN ln -s /usr/lib/libIfcGeom.so.0.7 /usr/lib/libIfcGeom.so
RUN mv /build/engine/build/cmake/_deps/ifcopenshell-build/libIfcParse.so.0.7.0 /usr/lib/
RUN ln -s /usr/lib/libIfcParse.so.0.7.0 /usr/lib/libIfcParse.so.0.7
RUN ln -s /usr/lib/libIfcParse.so.0.7 /usr/lib/libIfcParse.so
RUN rm -rf /build

CMD java -jar app.jar
