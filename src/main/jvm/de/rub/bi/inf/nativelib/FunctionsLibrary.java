package de.rub.bi.inf.nativelib;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;

public interface FunctionsLibrary extends Library {

    boolean initIfc(String fileName);

    boolean ifc_object_to_string(Pointer ifcPointer, Pointer outputStringPointer, long len);

    void init_function(
            get_input_pointer gip,
            get_input_double gid,
            get_input_integer gii,
            get_input_string gis,
            set_output_pointer sop,
            set_output_double sod,
            set_output_integer soi,
            set_output_string sos,
            set_output_collection soc);

    void filterByGUID();

    void filterByElement();

    void test();

    interface set_output_collection extends Callback {
        Pointer invoke(int at, int size);
    }

    interface get_input_pointer extends Callback {
        Pointer invoke(int at);
    }

    interface get_input_string extends Callback {
        String invoke(int at);
    }

    interface get_input_integer extends Callback {
        int invoke(int at);
    }

    interface get_input_double extends Callback {
        double invoke(int at);
    }

    interface set_output_pointer extends Callback {
        void invoke(int at, Pointer data);
    }

    interface set_output_string extends Callback {
        void invoke(int at, String data);
    }

    interface set_output_integer extends Callback {
        void invoke(int at, int data);
    }

    interface set_output_double extends Callback {
        void invoke(int at, double data);
    }

}
