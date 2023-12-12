// IfcOpenShell
#include "ifcparse/Ifc4.h"

// local includes
#include "include/ifc_init.hpp"

bool initIfc(JNA::String fileName)
{
    return OpenBIMRLEngine::initIfc(fileName);
}