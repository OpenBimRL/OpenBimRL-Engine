#include <ifcparse/Ifc2x3.h>

#include "./include/functions/filterByGUID.h"

void filterByGUID(void)
{
    auto file = OpenBIMRLEngine::getCurrentFile();

    auto ptr = file->instance_by_guid("guid");

    std::cout << ptr->as<Ifc2x3::IfcObject>()->GlobalId() << std::endl;
}