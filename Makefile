.PHONY: bin_dir

CC = clang++
LIB_ONLY_FLAGS = -static -Wall -Werror -I./src/main/cpp/include -I src/main/cpp
C_FLAGS = -std=c++20 -fPIC -O3
LD_FLAGS = -shared -lstdc++ -static-libstdc++ -static-libgcc
libs = /usr/local/lib/libIfcParse.a 
RESOURCES_DIR = src/main/resources
BIN_DIR = target/native/bin
SOURCE_DIR = src/main/cpp

# Sources
SOURCES := $(wildcard $(SOURCE_DIR)/*.cpp)
OBJECTS := $(patsubst $(SOURCE_DIR)/%.cpp, $(BIN_DIR)/%.o, $(SOURCES))
# Functions
FUNCTION_SOURCES := $(wildcard $(SOURCE_DIR)/functions/*.cpp)
FUNCTION_OBJECTS := $(patsubst $(SOURCE_DIR)/functions/%.cpp, $(BIN_DIR)/%.o, $(FUNCTION_SOURCES))
# Utils
UTILS_SOURCES := $(wildcard $(SOURCE_DIR)/utils/*.cpp)
UTILS_OBJECTS := $(patsubst $(SOURCE_DIR)/utils/%.cpp, $(BIN_DIR)/%.o, $(UTILS_SOURCES))

all: $(OBJECTS) $(FUNCTION_OBJECTS) $(UTILS_OBJECTS)
	$(CC) $(C_FLAGS) $(LD_FLAGS) $^ -o $(RESOURCES_DIR)/lib.so $(libs)

$(BIN_DIR)/%.o: $(SOURCE_DIR)/%.cpp bin_dir
	$(CC) $(LIB_ONLY_FLAGS) $(C_FLAGS) -c $< -o $@ 

$(BIN_DIR)/%.o: $(SOURCE_DIR)/functions/%.cpp bin_dir
	$(CC) $(LIB_ONLY_FLAGS) $(C_FLAGS) -c $< -o $@

$(BIN_DIR)/%.o: $(SOURCE_DIR)/utils/%.cpp bin_dir
	$(CC) $(LIB_ONLY_FLAGS) $(C_FLAGS) -c $< -o $@

bin_dir:
	mkdir -p $(BIN_DIR) 
	mkdir -p $(BIN_DIR)/functions
