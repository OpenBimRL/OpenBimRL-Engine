.PHONY: make_lib

CC = "clang"

prequisites:
	mkdir -p target/lib

compile: prequisites
	$(CC) -c -fPIC src/main/cpp/lib.c -o src/main/resources/test.o

make_lib: compile
	$(CC) -shared -o src/main/resources/test.so src/main/resources/test.o && rm src/main/resources/test.o