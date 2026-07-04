
CC ?= clang
CXX ?= clang++
OPENBIMRL_ENABLE_ROCM_OFFLOAD ?= OFF
OPENBIMRL_ROCM_OFFLOAD_ARCH ?=
OPENBIMRL_NATIVE_CACHE_DIR ?= $(HOME)/.cache/openbimrl/native
OPENBIMRL_USE_PREBUILT_IFCOPENSHELL ?= OFF
OPENBIMRL_IFCOPENSHELL_PREFIX ?= /opt/ifcopenshell

CMAKE_DIR := $(OPENBIMRL_NATIVE_CACHE_DIR)/cmake
CMAKE_SOURCE := src/main/cpp
CMAKE_LISTS := $(CMAKE_SOURCE)/CMakeLists.txt
CMAKE_CACHE := $(CMAKE_DIR)/CMakeCache.txt
CMAKE_CONFIG_STAMP := $(CMAKE_DIR)/.openbimrl_cmake_config
NATIVE_LIB := $(CMAKE_DIR)/libOpenBIMRL_Native.so
RESOURCE_LIB := src/main/resources/lib.so

CMAKE_GENERATOR :=
ifneq ($(shell command -v ninja 2>/dev/null),)
CMAKE_GENERATOR := -G Ninja
endif

CMAKE_CCACHE_FLAGS :=
ifneq ($(shell command -v ccache 2>/dev/null),)
CMAKE_CCACHE_FLAGS := -DCMAKE_C_COMPILER_LAUNCHER=ccache -DCMAKE_CXX_COMPILER_LAUNCHER=ccache
endif

.PHONY: install build clean _prepare_cmake

install: build
	@mkdir -p src/main/resources
	@if [ ! -f "$(RESOURCE_LIB)" ] || ! cmp -s "$(NATIVE_LIB)" "$(RESOURCE_LIB)"; then \
		echo "Updating $(RESOURCE_LIB) ..."; \
		cp "$(NATIVE_LIB)" "$(RESOURCE_LIB)"; \
	else \
		echo "Native library unchanged, skipping resource copy."; \
	fi

build: _prepare_cmake
	@export CXX="$(CXX)"; export CC="$(CC)"; \
	cmake --build "$(CMAKE_DIR)" -j $$(nproc --all) -t OpenBIMRL_Native --config Release

_prepare_cmake:
	@mkdir -p "$(CMAKE_DIR)"; \
	ROCM_ARCH="$(OPENBIMRL_ROCM_OFFLOAD_ARCH)"; \
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
	CMAKE_LISTS_HASH=$$(sha256sum "$(CMAKE_LISTS)" | awk '{print $$1}'); \
	CONFIG_KEY="$(OPENBIMRL_ENABLE_ROCM_OFFLOAD)|$$ROCM_ARCH|$$C_COMPILER|$$CXX_COMPILER|$(OPENBIMRL_USE_PREBUILT_IFCOPENSHELL)|$(OPENBIMRL_IFCOPENSHELL_PREFIX)|$$CMAKE_LISTS_HASH"; \
	if [ -f "$(CMAKE_CACHE)" ] && [ -f "$(CMAKE_CONFIG_STAMP)" ] && [ "$$(cat "$(CMAKE_CONFIG_STAMP)")" = "$$CONFIG_KEY" ]; then \
		echo "Using existing CMake cache in $(CMAKE_DIR)"; \
	else \
		echo "Configuring CMake in $(CMAKE_DIR) ..."; \
		cmake -B "$(CMAKE_DIR)" -S "$(CMAKE_SOURCE)" \
			$(CMAKE_GENERATOR) \
			$(CMAKE_CCACHE_FLAGS) \
			-DCMAKE_C_COMPILER=$$C_COMPILER \
			-DCMAKE_CXX_COMPILER=$$CXX_COMPILER \
			-DOPENBIMRL_ENABLE_ROCM_OFFLOAD=$(OPENBIMRL_ENABLE_ROCM_OFFLOAD) \
			-DOPENBIMRL_ROCM_OFFLOAD_ARCH=$$ROCM_ARCH \
			-DOPENBIMRL_USE_PREBUILT_IFCOPENSHELL=$(OPENBIMRL_USE_PREBUILT_IFCOPENSHELL) \
			-DOPENBIMRL_IFCOPENSHELL_PREFIX=$(OPENBIMRL_IFCOPENSHELL_PREFIX); \
		printf '%s' "$$CONFIG_KEY" > "$(CMAKE_CONFIG_STAMP)"; \
	fi

clean:
	rm -rf "$(CMAKE_DIR)"
