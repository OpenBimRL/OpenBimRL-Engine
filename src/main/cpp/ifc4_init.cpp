// IfcOpenShell
#include "ifcparse/Ifc4.h"

// local includes
#include "include/ifc_init.hpp"

#define IfcSchema Ifc4

bool initIfc4(char *fileName)
{
    return OpenBIMRLEngine::initIfc(fileName);
}