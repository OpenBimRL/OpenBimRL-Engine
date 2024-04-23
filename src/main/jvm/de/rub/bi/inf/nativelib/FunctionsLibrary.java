package de.rub.bi.inf.nativelib;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

/**
 * @author Florian Becker
 */
public interface FunctionsLibrary extends Library {

    boolean initIfc(String fileName);

    void init_function(
            get_input_pointer gip,
            get_input_double gid,
            get_input_integer gii,
            get_input_string gis,
            set_output_pointer sop,
            set_output_double sod,
            set_output_integer soi,
            set_output_string sos,
            set_output_array soa);

    void filterByGUID();

    void filterByElement();

    void test();

    NativeLong request_ifc_object_json_size(Pointer p);
    void ifc_object_to_json(Pointer buffer);

    interface set_output_array extends Callback {
        Pointer invoke(int at, NativeLong size);
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
