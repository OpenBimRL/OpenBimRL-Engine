cmake_minimum_required(VERSION 3.27)
project(cpp)

set(CMAKE_CXX_STANDARD 14)

include_directories(include)
include_directories(include/functions)
include_directories(include/nlohmann)
include_directories(include/types)
include_directories(include/utils)
include_directories(include/utils/OpenBIMRL)

add_executable(cpp
        functions/filterByElement.cpp
        functions/filterByGUID.cpp
        functions/function.cpp
        functions/getBoundingBox.cpp
        functions/test.cpp
        include/functions/filterByElement.hpp
        include/functions/filterByGUID.hpp
        include/functions/function.hpp
        include/functions/getBoundingBox.hpp
        include/functions/test.hpp
        include/nlohmann/json.hpp
        include/types/IFC_typedefs.hpp
        include/types/JNA_typedefs.hpp
        include/utils/OpenBIMRL/file.hpp
        include/utils/OpenBIMRL/ifcStandards.hpp
        include/utils/ifc_init.hpp
        include/utils/ifc_properties.hpp
        include/utils/ifc_quantities.hpp
        include/utils/ifcObjectToString.hpp
        include/lib.h
        include/structs.h
        include/types.hpp
        utils/ifc_init.cpp
        utils/ifc_properties.cpp
        utils/ifc_quantities.cpp
        utils/native_ifc_object_to_string.cpp
        utils/native_initIfc.cpp)
