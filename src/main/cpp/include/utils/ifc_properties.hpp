#ifndef IFC_PROPERTIES_H
#define IFC_PROPERTIES_H

#include "types.hpp"

extern "C"
{
    std::size_t initPropertyIterator(JNA::Pointer);
    std::size_t getBufferSizePropertySetName(std::size_t);
    std::size_t getBufferSizePropertyName(std::size_t, std::size_t);
    std::size_t getBufferSizePropertyValue(std::size_t, std::size_t);
    std::size_t getNoOfPropertiesInSet(std::size_t);
    bool getPropertySetName(std::size_t, JNA::Pointer);
    bool getPropertyName(std::size_t, std::size_t, JNA::Pointer);
    bool getPropertyValue(std::size_t, std::size_t, JNA::Pointer);
}
#endif