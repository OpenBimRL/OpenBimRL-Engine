FROM rocm/dev-ubuntu-22.04:7.1.1 AS rocm-llvm
FROM mcr.microsoft.com/devcontainers/java:1-21-bookworm

USER root

COPY --from=rocm-llvm /opt/rocm-7.1.1/ /opt/rocm/

RUN apt-get update && apt-get install -y --no-install-recommends \
    xfonts-scalable libocct-data-exchange-dev libocct-draw-dev \
    libocct-foundation-dev libocct-modeling-algorithms-dev libocct-modeling-data-dev libocct-ocaf-dev \
    libocct-visualization-dev libboost-all-dev libhdf5-dev libmpfr-dev libgmp3-dev libxml2-dev cmake \
    && ln -sf /opt/rocm/llvm/bin/clang /usr/local/bin/clang \
    && ln -sf /opt/rocm/llvm/bin/clang++ /usr/local/bin/clang++ \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN bash -c "source /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven"

RUN git clone https://github.com/RUB-Informatik-im-Bauwesen/OpenBimRL.git api
RUN cd api && git checkout 9699b39 && mvn install
RUN rm -rf api

ENV ROCM_PATH=/opt/rocm
ENV PATH=${ROCM_PATH}/llvm/bin:${PATH}
ENV CC=${ROCM_PATH}/llvm/bin/clang
ENV CXX=${ROCM_PATH}/llvm/bin/clang++
