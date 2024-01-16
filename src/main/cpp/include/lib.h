#ifndef LIB_H
#define LIB_H

#include <cstdint>
#include "JNA_typedefs.hpp"

extern "C" bool ifc_object_to_string(JNA::Pointer, JNA::Pointer, std::size_t);


bool init();

#endif