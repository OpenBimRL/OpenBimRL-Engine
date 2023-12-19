.PHONY: bin_dir

CC = clang++
C_FLAGS = -std=c++20 -fPIC -Wall
LD_FLAGS = -shared -lstdc++ 
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

all: $(OBJECTS) $(FUNCTION_OBJECTS)
	$(CC) $(C_FLAGS) $(LD_FLAGS) $^ -o $(RESOURCES_DIR)/lib.so $(libs)

$(BIN_DIR)/%.o: $(SOURCE_DIR)/%.cpp bin_dir
	$(CC) $(C_FLAGS) -c $< -o $@ -I src/main/cpp

$(BIN_DIR)/%.o: $(SOURCE_DIR)/functions/%.cpp bin_dir
	$(CC) $(C_FLAGS) -c $< -o $@ -I src/main/cpp

bin_dir:
	mkdir -p $(BIN_DIR) 
	mkdir -p $(BIN_DIR)/functions
