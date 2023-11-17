package de.rub.bi.inf.nativelib;

import com.sun.jna.Library;

public interface FunctionsLibrary extends Library {

  public int sum (int n1, int n2);
  public boolean initIfc4 (String fileName);
  public boolean initIfc2x3 (String fileName);
  
}
