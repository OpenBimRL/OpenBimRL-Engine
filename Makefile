
install: build
	mv build/cmake/libOpenBIMRL_Native.so src/main/resources/lib.so

build: _prepare_cmake
	export CXX="clang++"; export CC="clang"; cmake --build build/cmake/ -j 5 -t OpenBIMRL_Native --config Release

_prepare_cmake:
	 cmake -B build/cmake/ -S src/main/cpp

clean:
	rm -rf build/cmake