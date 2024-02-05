#ifndef IFC_QUANTITIES_H
#define IFC_QUANTITIES_H

#include "types.hpp"

extern "C"
{
    std::size_t initQuantityIterator(JNA::Pointer);
    std::size_t getBufferSizeQuantitySetName(std::size_t);
    std::size_t getBufferSizeQuantityName(std::size_t, std::size_t);
    // std::size_t getBufferSizeQuantityValue(std::size_t, std::size_t);
    std::size_t getNoOfQuantitiesInSet(std::size_t);
    bool getQuantitySetName(std::size_t, JNA::String);
    bool getQuantityName(std::size_t, std::size_t, JNA::String);
    double getQuantityValue(std::size_t, std::size_t);
}
#endif