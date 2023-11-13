.PHONY: make_lib

CC = "clang"

prequisites:
	mkdir -p target/lib

compile: prequisites
	$(CC) -std=c++11 -c -fPIC src/main/cpp/lib.cpp -o src/main/resources/test.o

make_lib: compile
	$(CC) -shared -std=c++11 -lstdc++ -o src/main/resources/test.so src/main/resources/test.o && rm src/main/resources/test.o
