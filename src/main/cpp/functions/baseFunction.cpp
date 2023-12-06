#include "include/functions/function.hpp"

template <typename T>
inline T getInputAs(uint32_t at)
{
    return dynamic_cast<T>(getInput(at));
}