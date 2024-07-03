FROM mcr.microsoft.com/devcontainers/java:1-21-bookworm

RUN sudo apt update && sudo apt install -y xfonts-scalable libocct-data-exchange-dev libocct-draw-dev \
    libocct-foundation-dev libocct-modeling-algorithms-dev libocct-modeling-data-dev libocct-ocaf-dev \
    libocct-visualization-dev libboost-all-dev libhdf5-dev libmpfr-dev libgmp3-dev libxml2-dev clang cmake

RUN bash -c "source /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven"

RUN git clone https://github.com/RUB-Informatik-im-Bauwesen/OpenBimRL.git api
RUN cd api && git checkout 9699b39 && mvn install
RUN rm -rf api

ENV CC="clang"
ENV CXX="clang++"
