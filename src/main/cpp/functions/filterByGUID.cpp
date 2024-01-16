#include <ifcparse/IfcSchema.h>

#include "../include/functions/filterByGUID.hpp"

void filterByGUID()
{
    JNA::String input = getInputString(0);
    if (input == nullptr)
        return;

    auto guid = std::string(input); // create c++ string from char*
    IfcParse::IfcFile *file = OpenBIMRLEngine::getCurrentFile();

    IfcUtil::IfcBaseClass *ptr = nullptr;

    try
    {
        ptr = file->instance_by_guid(guid);
    }
    catch (const IfcParse::IfcException &)
    {
    }

    setOutputPointer(0, ptr);
}