#include "types.hpp"
#include "utils/ifcObjectToString.hpp"

/// @brief writes IfcType#GUID to stringPointer
/// @param ifcObject only works if GUID is at 0th position
/// @param stringPointer pointer to free memory (idealy zeros)
/// @param maxLen string maximum size
bool ifc_object_to_string(const JNA::Pointer ifcObject, JNA::Pointer stringPointer, const std::size_t maxLen)
{
    if (ifcObject == nullptr)
        return false;

    const auto data = static_cast<IfcUtil::IfcBaseClass *>(ifcObject)->data();
    std::stringstream ss;

    // remove quotes from string
    auto guid = data.getArgument(0)->toString();
    guid.erase(std::remove(guid.begin(), guid.end(), '\''), guid.end());

    ss << data.type()->name() << '#' << guid;
    const auto str = ss.str();
    std::memcpy(stringPointer, str.c_str(), std::min(maxLen, str.size()));

    return true;
}