    #ifndef IFC_INIT_H
#define IFC_INIT_H

#include "lib.h"

#include "ifcparse/IfcFile.h"


extern "C"
{
    bool initIfc4(char *fileName);
    bool initIfc2x3(char *fileName);
}


namespace OpenBIMRLEngine
{
    IfcParse::IfcFile *getCurrentFile();
    bool initIfc(char *fileName);
} // namespace OpenBIMRLEngine


#endif