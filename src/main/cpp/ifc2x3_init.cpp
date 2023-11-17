// IfcOpenShell
#include "ifcparse/Ifc2x3.h"

// local includes
#include "include/ifc_init.hpp"

#define IfcSchema Ifc2x3

bool initIfc2x3(char *fileName)
{
    return OpenBIMRLEngine::initIfc(fileName);
}