#include <stdint.h>
#include <string>
#include "../JNA_typedefs.hpp"

// input function
typedef JNA::Pointer (*get_input_pointer)(uint32_t);
typedef double (*get_input_double)(uint32_t);
typedef uint32_t (*get_input_integer)(uint32_t);
typedef JNA::String (*get_input_string)(uint32_t);

// output functions
typedef void (*set_output_pointer)(uint32_t, JNA::Pointer);
typedef void (*set_output_double)(uint32_t, double);
typedef void (*set_output_integer)(uint32_t, uint32_t);
typedef void (*set_output_string)(uint32_t, JNA::String);

get_input_pointer getInputPointer;
get_input_double getInputDouble;
get_input_integer getInputInt;
get_input_string getInputString;

set_output_pointer setOutputPointer;
set_output_double setOutputDouble;
set_output_integer setOutputInt;
set_output_string setOutputString;

extern "C" void init_function(get_input_pointer gip,
                              get_input_double gid,
                              get_input_integer gii,
                              get_input_string gis,
                              set_output_pointer sop,
                              set_output_double sod,
                              set_output_integer soi,
                              set_output_string sos)
{
    getInputPointer = gip;
    getInputDouble = gid;
    getInputInt = gii;
    getInputString = gis;

    setOutputPointer = sop;
    setOutputDouble = sod;
    setOutputInt = soi;
    setOutputString = sos;
}