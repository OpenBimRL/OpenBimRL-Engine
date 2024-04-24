# syntax=docker/dockerfile:1
FROM maven:3.9.6-amazoncorretto-21-debian-bookworm
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

ENV CC=clang
ENV CXX=clang++

COPY . /build/engine
RUN cd /build/engine && mvn install -Dmaven.test.skip -X --quiet && mvn package -Dmaven.test.skip --quiet  # build (and test package [in the future...])

RUN bash -c "cp /build/engine/target/*-jar-with-dependencies.jar app.jar"
RUN cp /build/engine/src/main/resources/lib.so .

RUN rm -rf /build

CMD java -jar app.jar