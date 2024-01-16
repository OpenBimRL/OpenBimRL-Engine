#include <ifcparse/IfcSchema.h>

#include "../include/functions/filterByElement.hpp"

typedef IfcUtil::IfcBaseClass *IfcObjectPointer;

void filterByElement(void)
{

    JNA::String input = OpenBIMRLEngine::Functions::getInputString(0); // get input from graph

    // check for null
    if (input == nullptr)
    {
        OpenBIMRLEngine::Functions::setOutputPointer(0, new IfcObjectPointer[0]); // return empty collection
        return;
    }

    auto ifcClass = std::string(input); // create c++ string from char*

    IfcParse::IfcFile *file = OpenBIMRLEngine::getCurrentFile(); // get active file

    auto instances = file->instances_by_type_excl_subtypes(ifcClass); // get instances by ifc class excluding subtypes

    auto buffer = (IfcObjectPointer *)OpenBIMRLEngine::Functions::setOutputArray(0, instances->size());

    std::copy(instances->begin(), instances->end(), buffer);
}