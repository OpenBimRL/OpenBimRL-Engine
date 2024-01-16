#ifndef FUNCT_HPP
#define FUNCT_HPP

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

namespace OpenBIMRLEngine::Functions
{

    extern get_input_pointer getInputPointer;
    extern get_input_double getInputDouble;
    extern get_input_integer getInputInt;
    extern get_input_string getInputString;

    extern set_output_pointer setOutputPointer;
    extern set_output_double setOutputDouble;
    extern set_output_integer setOutputInt;
    extern set_output_string setOutputString;

}

extern "C" void init_function(get_input_pointer,
                              get_input_double,
                              get_input_integer,
                              get_input_string,
                              set_output_pointer,
                              set_output_double,
                              set_output_integer,
                              set_output_string);

#endif