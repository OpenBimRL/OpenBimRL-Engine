.DEFAULT_GOAL := lib.so
.PHONY: housekeeping

CC = "clang"
C_FLAGS = -std=c++17
LD_FLAGS = -shared -lstdc++

housekeeping:
	rm src/main/resources/lib.o 

lib.o:
	$(CC) $(C_FLAGS) -c -fPIC src/main/cpp/lib.cpp -o src/main/resources/lib.o

lib.so: lib.o
	$(CC) $(C_FLAGS) $(LD_FLAGS) -o src/main/resources/lib.so src/main/resources/lib.o && make housekeeping
