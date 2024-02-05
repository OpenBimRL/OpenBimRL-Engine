// system includes
#include <ifcparse/IfcSchema.h>
#include <ifcparse/IfcFile.h>

// local includes
#include "functions/filterByElement.hpp"
#include "utils/OpenBIMRL/file.hpp"
#include "types.hpp"

void filterByElement(void)
{

    JNA::String input = OpenBIMRLEngine::Functions::getInputString(0); // get input from graph

    // check for null
    if (input == nullptr)
    {
        OpenBIMRLEngine::Functions::setOutputPointer(0, nullptr);
        return;
    }

    const auto ifcClass = std::string(input); // create c++ string from char*

    IfcParse::IfcFile *file = OpenBIMRLEngine::getCurrentFile(); // get active file

    const auto instances = file->instances_by_type(ifcClass); // get instances by ifc class excluding subtypes
    const auto size = instances->size();
    if (!size)
    {
        OpenBIMRLEngine::Functions::setOutputPointer(0, nullptr);
        return;
    }

    const auto buffer = (IfcObjectPointer *)OpenBIMRLEngine::Functions::setOutputArray(0, size);

    std::copy(instances->begin(), instances->end(), buffer);
}