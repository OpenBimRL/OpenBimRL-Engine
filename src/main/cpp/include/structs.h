#include <stdint.h>

extern "C"
{
    typedef struct
    {
        uint32_t lenght;
        bool isArray;
        void *data;
    } FunctionParams;
}