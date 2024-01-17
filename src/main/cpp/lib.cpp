// system includes
#include <iostream>

#include <ifcparse/IfcLogger.h>
#include <ifcparse/IfcFile.h>

// local includes
#include "lib.h"
#include "types.hpp"


/// @brief writes IfcType#GUID to stringPointer
/// @param ifcObject only works if GUID is at 0th position
/// @param stringPointer pointer to free memory (idealy zeros)
/// @param maxLen string maximum size
bool ifc_object_to_string(JNA::Pointer ifcObject, JNA::Pointer stringPointer, std::size_t maxLen)
{
    if (ifcObject == nullptr)
        return false;

    auto data = static_cast<IfcUtil::IfcBaseClass *>(ifcObject)->data();
    std::stringstream ss;

    // remove quotes from string
    auto guid = data.getArgument(0)->toString();
    guid.erase( std::remove( guid.begin(), guid.end(), '\'' ), guid.end() );

    ss << data.type()->name() << '#' << guid;
    auto str = ss.str();
    std::memcpy(stringPointer, str.c_str(), std::min(maxLen, str.length()));

    return true;
}

bool init()
{
    // Redirect the output (both progress and log) to stdout
    Logger::SetOutput(&std::cout, &std::cout);

    return true;
}