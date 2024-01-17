// libstdc++
#include <iostream>

// local includes
#include "utils/ifc_init.hpp"

#include "utils/OpenBIMRL/file.hpp"
#include "utils/OpenBIMRL/ifcStandards.hpp"

static IfcParse::IfcFile *currentFile;

static bool init()
{
    // Redirect the output (both progress and log) to stdout
    Logger::SetOutput(&std::cout, &std::cout);

    return true;
}

// hopefully this doesn't leak memory... ¯\_(ツ)_/¯
static void setCurrentFile(IfcParse::IfcFile *newFile)
{
    if (currentFile)
        delete currentFile;

    currentFile = newFile;
}

bool OpenBIMRLEngine::initIfc(JNA::String fileName)
{
    if (!init())
    {
        std::cerr << "failed to initialize IFC libary" << std::endl;
        return false;
    }

    if (!fileName)
    {
        std::cerr << "no file given" << std::endl;
        return false;
    }

    // Parse the IFC file provided in fileName
    auto file = new IfcParse::IfcFile(fileName);
    if (!file->good())
    {
        std::cerr << "Unable to parse .ifc file" << std::endl;
        return false;
    }

    setCurrentFile(file);

    return true;
}

inline bool OpenBIMRLEngine::isIFC4()
{
    return getCurrentFile()->schema()->name() == "IFC4";
}

inline bool OpenBIMRLEngine::isIFC2x3()
{
    return getCurrentFile()->schema()->name() == "IFC2x3";
}

IfcParse::IfcFile *OpenBIMRLEngine::getCurrentFile()
{
    return currentFile;
}