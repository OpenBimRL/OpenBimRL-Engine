#include <ifcparse/IfcSchema.h>

#include "./include/functions/filterByGUID.hpp"

void filterByGUID()
{
    JNA::String input = getInputString(0);
    if (input == nullptr)
        return;

    auto guid = std::string(input); // create c++ string from char*
    IfcParse::IfcFile *file = OpenBIMRLEngine::getCurrentFile();

    IfcUtil::IfcBaseClass *ptr = file->instance_by_guid(guid);

    setOutputPointer(0, ptr);
}