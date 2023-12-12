package de.rub.bi.inf.nativelib;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Callback;

public interface FunctionsLibrary extends Library {

  public int sum(int n1, int n2);

  public boolean initIfc(String fileName);

  public void ifc_object_to_string(Pointer ifcPointer, Pointer outputStringPointer, long len);

  public void init_function(
      get_input_pointer gip,
      get_input_double gid,
      get_input_integer gii,
      get_input_string gis,
      set_output_pointer sop,
      set_output_double sod,
      set_output_integer soi,
      set_output_string sos);

  public void filterByGUID();

  public static interface get_input_pointer extends Callback {
    Pointer invoke(int at);
  }

  public static interface get_input_string extends Callback {
    String invoke(int at);
  }

  public static interface get_input_integer extends Callback {
    int invoke(int at);
  }

  public static interface get_input_double extends Callback {
    double invoke(int at);
  }

  public static interface set_output_pointer extends Callback {
    void invoke(int at, Pointer data);
  }

  public static interface set_output_string extends Callback {
    void invoke(int at, String data);
  }

  public static interface set_output_integer extends Callback {
    void invoke(int at, int data);
  }

  public static interface set_output_double extends Callback {
    void invoke(int at, double data);
  }

}
