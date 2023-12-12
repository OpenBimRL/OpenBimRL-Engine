#ifndef IFC_INIT_H
#define IFC_INIT_H

#include "lib.h"

#include "ifcparse/IfcFile.h"


extern "C" bool initIfc(JNA::String fileName);

namespace OpenBIMRLEngine
{
    IfcParse::IfcFile *getCurrentFile();
    bool initIfc(JNA::String fileName);
    bool isIFC4();

} // namespace OpenBIMRLEngine


#endif