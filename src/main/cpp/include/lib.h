#ifndef LIB_H
#define LIB_H

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

#endif