# syntax=docker/dockerfile:1
FROM maven:3.9.9-eclipse-temurin-23-noble
USER root

RUN apt update && apt install -yq clang make cmake git \
    # lib occt:
    xfonts-scalable libocct-data-exchange-dev libocct-draw-dev libocct-foundation-dev libocct-modeling-algorithms-dev \
    libocct-modeling-data-dev libocct-ocaf-dev libocct-visualization-dev \
    # other dependencies:
    libmpfr-dev libboost-all-dev libhdf5-dev libgmp-dev

WORKDIR /app
RUN git clone --quiet https://github.com/RUB-Informatik-im-Bauwesen/OpenBimRL.git /build/api
RUN cd /build/api && git checkout 9699b39 && mvn install --quiet

RUN git clone --quiet https://github.com/RUB-Informatik-im-Bauwesen/Maven-Bounding-Volume-Hierarchy.git /build/bvh
RUN cd /build/bvh && mvn install --quiet

ENV CC=clang
ENV CXX=clang++

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