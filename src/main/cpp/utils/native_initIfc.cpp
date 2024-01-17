// IfcOpenShell
#include <ifcparse/Ifc4.h>

// local includes
#include "utils/OpenBIMRL/file.hpp"

bool initIfc(JNA::String fileName)
{
    return OpenBIMRLEngine::initIfc(fileName);
}