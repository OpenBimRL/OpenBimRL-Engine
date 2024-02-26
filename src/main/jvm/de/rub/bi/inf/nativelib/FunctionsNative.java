package de.rub.bi.inf.nativelib;

import com.sun.jna.Native;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FunctionsNative {

    private static FunctionsLibrary functionsNative;

    public static FunctionsLibrary getInstance() {
        if (functionsNative != null)
            return functionsNative;
        throw new UnsatisfiedLinkError("library not loaded. Try calling `new FunctionsNative(libName)`");
    }

    public static void create(String fileName) throws IOException {
        functionsNative = Native.load(extractFile(fileName), FunctionsLibrary.class);
    }

    private static String extractFile(final String fileName) throws IOException {
        final InputStream source = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName);
        final File file = File.createTempFile("lib", null);
        FileUtils.copyInputStreamToFile(source, file);
        return file.getAbsolutePath();
    }

}
