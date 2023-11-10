package de.rub.bi.inf.nativelib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;

import io.github.classgraph.ClassGraph;

import org.apache.commons.io.FileUtils;

import com.sun.jna.Native;

public class FunctionsNative implements FunctionsLibrary {

  private final FunctionsLibrary functionsNative;

  public FunctionsNative(final String fileName) throws IOException {
    functionsNative = Native.loadLibrary(extractFile(fileName), FunctionsLibrary.class);
  }

  @Override
  public int sum(final int n1, final int n2) {
    return functionsNative.sum(n1, n2);
  }

  private String extractFile(final String fileName) throws IOException {
    final InputStream source = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName);
    final File file = File.createTempFile("lib", null);
    FileUtils.copyInputStreamToFile(source, file);
    return file.getAbsolutePath();
  }

}
