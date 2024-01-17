#include "types.hpp"

#include "utils/ifc_init.hpp"
#include "utils/ifcObjectToString.hpp"

// an incomplete collection
extern "C" {
    extern bool initIfc(JNA::String);
    extern bool ifc_object_to_string(JNA::Pointer, JNA::Pointer, std::size_t);
}