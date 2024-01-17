#ifndef IFC_INIT_H
#define IFC_INIT_H

// system includes
#include <ifcparse/IfcFile.h>

// local includes
#include "lib.h"
#include "types.hpp"

/// @brief initialize library and load file into memory
/// @param path to file
/// @return true if the library was successfully loaded
extern "C" bool initIfc(JNA::String);

#endif