#include <iostream>
#include "include/nlohmann/json.hpp"
#include "include/lib.h"

#include "ifcparse/IfcLogger.h"

struct returnType
{
    char *type;
    int size;
    void *content[];
};

int sum(int n1, int n2)
{
    std::cout << "calculating: " << n1 << " + " << n2 << std::endl;
    return n1 + n2;
}

bool init()
{
    // Redirect the output (both progress and log) to stdout
    Logger::SetOutput(&std::cout, &std::cout);

    return true;
}