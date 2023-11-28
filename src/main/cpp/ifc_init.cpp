//libstdc++
#include <iostream>

// local includes
#include "include/ifc_init.hpp"
#include "include/functions/filterByGUID.hpp"

static IfcParse::IfcFile *currentFile;

// hopefully this doesn't leak memory... ¯\_(ツ)_/¯
void setCurrentFile(IfcParse::IfcFile *newFile) {
    if (currentFile) {
        delete currentFile;
    }
    currentFile = newFile;
}

bool OpenBIMRLEngine::initIfc(char *fileName)
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

    OpenBIMRLEngine::functions::exec((const char *)"IfcWall");

    return true;
}

IfcParse::IfcFile *OpenBIMRLEngine::getCurrentFile() {
    return currentFile;
}