#ifndef OPENBIMRL_FILE_H
#define OPENBIMRL_FILE_H

#include <ifcparse/IfcFile.h>

#include "types.hpp"

namespace OpenBIMRLEngine {
    IfcParse::IfcFile *getCurrentFile();
    bool initIfc(JNA::String);
}

#endif