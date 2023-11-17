.PHONY: bin_dir

CC = clang++
C_FLAGS = -std=c++20 -fPIC -I/home/remote/IfcOpenshell/src
LD_FLAGS = -shared -lstdc++
RESOURCES_DIR = src/main/resources
BIN_DIR = target/native/bin
SOURCE_DIR = src/main/cpp

SOURCES := $(wildcard $(SOURCE_DIR)/*.cpp)
OBJECTS := $(patsubst $(SOURCE_DIR)/%.cpp, $(BIN_DIR)/%.o, $(SOURCES))

all: $(OBJECTS)
	$(CC) $(C_FLAGS) $(LD_FLAGS) $^ -o $(RESOURCES_DIR)/lib.so /home/remote/IfcOpenshell/build/libIfcParse.a

$(BIN_DIR)/%.o: $(SOURCE_DIR)/%.cpp bin_dir
	$(CC) $(C_FLAGS) -c $< -o $@

bin_dir:
	mkdir -p $(BIN_DIR)

# lib.o: bin_dir
# 	$(CC) $(C_FLAGS) -c $(SOURCE_DIR)/lib.cpp -o $(BIN_DIR)/lib.o

# lib.so: lib.o
# 	$(CC) $(C_FLAGS) $(LD_FLAGS) -o $(RESOURCES_DIR)/lib.so $(BIN_DIR)/lib.o
