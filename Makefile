
CC ?= clang
CXX ?= clang++
OPENBIMRL_ENABLE_ROCM_OFFLOAD ?= OFF
OPENBIMRL_ROCM_OFFLOAD_ARCH ?=

install: build
	mv build/cmake/libOpenBIMRL_Native.so src/main/resources/lib.so

build: _prepare_cmake
	export CXX="$(CXX)"; export CC="$(CC)"; cmake --build build/cmake/ -j $(shell nproc --all) -t OpenBIMRL_Native --config Release

_prepare_cmake:
	@ROCM_ARCH="$(OPENBIMRL_ROCM_OFFLOAD_ARCH)"; \
	C_COMPILER="$(CC)"; \
	CXX_COMPILER="$(CXX)"; \
	if [ "$(OPENBIMRL_ENABLE_ROCM_OFFLOAD)" = "ON" ] && [ -z "$$ROCM_ARCH" ]; then \
		ROCMINFO_BIN="/opt/rocm/bin/rocminfo"; \
		if [ ! -x "$$ROCMINFO_BIN" ]; then \
			echo "ERROR: ROCm offloading is ON but $$ROCMINFO_BIN was not found."; \
			exit 1; \
		fi; \
		ROCM_ARCH=`$$ROCMINFO_BIN | grep -m1 -o 'gfx[0-9a-z]*'`; \
		if [ -z "$$ROCM_ARCH" ]; then \
			echo "ERROR: Could not detect a GPU arch from rocminfo output."; \
			exit 1; \
		fi; \
		echo "Detected ROCm offload arch: $$ROCM_ARCH"; \
	fi; \
	if [ "$(OPENBIMRL_ENABLE_ROCM_OFFLOAD)" = "ON" ]; then \
		if [ -x "/opt/rocm/llvm/bin/clang" ] && [ -x "/opt/rocm/llvm/bin/clang++" ]; then \
			C_COMPILER="/opt/rocm/llvm/bin/clang"; \
			CXX_COMPILER="/opt/rocm/llvm/bin/clang++"; \
		else \
			echo "WARNING: ROCm offloading is ON but /opt/rocm/llvm/bin/clang(++) was not found. Using CC/CXX from environment."; \
		fi; \
	fi; \
	rm -f build/cmake/CMakeCache.txt; \
	cmake -B build/cmake/ -S src/main/cpp \
		-DCMAKE_C_COMPILER=$$C_COMPILER \
		-DCMAKE_CXX_COMPILER=$$CXX_COMPILER \
		-DOPENBIMRL_ENABLE_ROCM_OFFLOAD=$(OPENBIMRL_ENABLE_ROCM_OFFLOAD) \
		-DOPENBIMRL_ROCM_OFFLOAD_ARCH=$$ROCM_ARCH

clean:
	rm -rf build/cmake