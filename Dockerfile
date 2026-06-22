# syntax=docker/dockerfile:1
FROM rocm/dev-ubuntu-22.04:7.1.1 AS rocm-llvm
FROM maven:3.9.9-eclipse-temurin-23-noble
USER root

ARG ENABLE_ROCM_OFFLOAD=OFF
ARG ROCM_OFFLOAD_ARCH=

COPY --from=rocm-llvm /opt/rocm-7.1.1/ /opt/rocm/

RUN apt update && apt install -yq make cmake git \
    # lib occt:
    xfonts-scalable libocct-data-exchange-dev libocct-draw-dev libocct-foundation-dev libocct-modeling-algorithms-dev \
    libocct-modeling-data-dev libocct-ocaf-dev libocct-visualization-dev \
    # other dependencies:
    libmpfr-dev libboost-all-dev libhdf5-dev libgmp-dev \
    && ln -sf /opt/rocm/llvm/bin/clang /usr/local/bin/clang \
    && ln -sf /opt/rocm/llvm/bin/clang++ /usr/local/bin/clang++

WORKDIR /app
RUN git clone --quiet https://github.com/RUB-Informatik-im-Bauwesen/OpenBimRL.git /build/api
RUN cd /build/api && git checkout 83bd65f && mvn -Dproject.build.sourceEncoding=ISO-8859-1 compiler:compile jar:jar install:install -DgroupId=inf.bi.rub.de -DartifactId=OpenBIMRL-API -Dversion=2023.07.1 -Dpackaging=jar --quiet

RUN git clone --quiet https://github.com/RUB-Informatik-im-Bauwesen/Maven-Bounding-Volume-Hierarchy.git /build/bvh
RUN cd /build/bvh && mvn install --quiet

ENV ROCM_PATH=/opt/rocm
ENV PATH=${ROCM_PATH}/llvm/bin:${PATH}
ENV CC=${ROCM_PATH}/llvm/bin/clang
ENV CXX=${ROCM_PATH}/llvm/bin/clang++
ENV OPENBIMRL_ENABLE_ROCM_OFFLOAD=${ENABLE_ROCM_OFFLOAD}
ENV OPENBIMRL_ROCM_OFFLOAD_ARCH=${ROCM_OFFLOAD_ARCH}

COPY . /build/engine
RUN cd /build/engine && mvn install -Dmaven.test.skip -X --quiet && mvn package -Dmaven.test.skip --quiet  # build (and test package [in the future...])

RUN bash -c "cp /build/engine/target/*-jar-with-dependencies.jar app.jar"
RUN mv /build/engine/build/cmake/_deps/ifcopenshell-build/libIfcGeom.so.0.7.0 /usr/lib/
RUN ln -s /usr/lib/libIfcGeom.so.0.7.0 /usr/lib/libIfcGeom.so.0.7
RUN ln -s /usr/lib/libIfcGeom.so.0.7 /usr/lib/libIfcGeom.so
RUN mv /build/engine/build/cmake/_deps/ifcopenshell-build/libIfcParse.so.0.7.0 /usr/lib/
RUN ln -s /usr/lib/libIfcParse.so.0.7.0 /usr/lib/libIfcParse.so.0.7
RUN ln -s /usr/lib/libIfcParse.so.0.7 /usr/lib/libIfcParse.so
RUN rm -rf /build

CMD java -jar app.jar