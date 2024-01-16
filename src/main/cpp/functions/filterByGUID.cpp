#include <ifcparse/IfcSchema.h>

#include "../include/functions/filterByGUID.hpp"

void filterByGUID()
{
    IfcUtil::IfcBaseClass *ptr = nullptr; // return pointer

    JNA::String input = OpenBIMRLEngine::Functions::getInputString(0); // get input from graph

    // check for null
    if (input == nullptr)
    {
        OpenBIMRLEngine::Functions::setOutputPointer(0, ptr); // return null
        return;
    }

    auto guid = std::string(input); // create c++ string from char*
    IfcParse::IfcFile *file = OpenBIMRLEngine::getCurrentFile(); // get active file

    try
    {
        ptr = file->instance_by_guid(guid); // find ifc obj by guid
    }
    // thank you IfcOpenShell for documenting that this error exists...
    catch (const IfcParse::IfcException &)
    {
    }

    // set Pointer to ifc obj
    OpenBIMRLEngine::Functions::setOutputPointer(0, ptr);
}