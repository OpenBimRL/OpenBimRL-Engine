#ifndef LIB_H
#define LIB_H

#include <map>
#include <stdint.h>
#include <stdio.h>

typedef struct
{
    int size;
    void *content;
} ReturnType;

static std::map<uint32_t, ReturnType> returnTypes;

extern "C"
{
    int sum(int n1, int n2);
    typedef struct
    {
        int a;
        int b;
    } mystruct;
}

bool init();

inline bool registerType(uint32_t magicByte, ReturnType type)
{
    return returnTypes.emplace(magicByte, type).second;
}

#endif