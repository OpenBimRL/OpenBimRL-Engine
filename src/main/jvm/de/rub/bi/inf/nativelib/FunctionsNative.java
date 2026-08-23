package de.rub.bi.inf.nativelib;

import com.sun.jna.Native;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class FunctionsNative {

    /**
     * Classpath resource name of the embedded amd64/x86_64 native shared library.
     * Stable (no version in the filename) so JNA lookup stays valid across Maven GAV bumps;
     * product/engine version is carried by the published jar coordinates.
     */
    public static final String NATIVE_LIBRARY_RESOURCE = "libOpenBimRL-Engine-Native-x86_64.so";

    private static FunctionsLibrary functionsNative;

    public static FunctionsLibrary getInstance() {
        if (functionsNative != null)
            return functionsNative;
        throw new UnsatisfiedLinkError("library not loaded. Try calling `FunctionsNative.create()`");
    }

    /** Load the packaged amd64 native library from the classpath. */
    public static void create() throws IOException {
        create(NATIVE_LIBRARY_RESOURCE);
    }

    public static void create(String fileName) throws IOException {
        functionsNative = Native.load(extractFile(fileName), FunctionsLibrary.class);
    }

    private static String extractFile(final String fileName) throws IOException {
        final InputStream source = FunctionsNative.class.getClassLoader().getResourceAsStream(fileName);
        if (source == null) {
            throw new IOException("Native library resource not found on classpath: " + fileName);
        }
        final File file = File.createTempFile("lib", null);
        FileUtils.copyInputStreamToFile(source, file);
        return file.getAbsolutePath();
    }

}
