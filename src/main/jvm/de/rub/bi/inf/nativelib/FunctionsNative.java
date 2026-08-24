package de.rub.bi.inf.nativelib;

import com.sun.jna.Native;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


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
        final File file = File.createTempFile("lib", null);
        FileUtils.writeByteArrayToFile(file, readNativeResourceBytes(fileName));
        return file.getAbsolutePath();
    }

    /**
     * Classpath root (Engine as Bazel root) or nested path when Engine is consumed as
     * an external bzlmod repo (e.g. external/openbimrl_engine+/…).
     */
    private static byte[] readNativeResourceBytes(String fileName) throws IOException {
        ClassLoader cl = FunctionsNative.class.getClassLoader();
        InputStream direct = cl.getResourceAsStream(fileName);
        if (direct != null) {
            try (direct) {
                return direct.readAllBytes();
            }
        }

        String suffix = "/" + fileName;
        for (URL url : classpathJarUrls()) {
            if (!"jar".equals(url.getProtocol()) && !url.getPath().endsWith(".jar")) {
                continue;
            }
            URL jarUrl = "jar".equals(url.getProtocol()) ? url : new URL("jar:" + url + "!/");
            JarURLConnection connection = (JarURLConnection) jarUrl.openConnection();
            connection.setUseCaches(false);
            try (JarFile jar = connection.getJarFile()) {
                for (JarEntry entry : Collections.list(jar.entries())) {
                    String name = entry.getName();
                    if (name.equals(fileName) || name.endsWith(suffix)) {
                        try (InputStream in = jar.getInputStream(entry)) {
                            return in.readAllBytes();
                        }
                    }
                }
            }
        }
        throw new IOException("Native library resource not found on classpath: " + fileName);
    }

    private static List<URL> classpathJarUrls() throws MalformedURLException {
        List<URL> urls = new ArrayList<>();
        for (String entry : System.getProperty("java.class.path", "").split(File.pathSeparator)) {
            if (entry.isEmpty() || !entry.endsWith(".jar")) {
                continue;
            }
            urls.add(new File(entry).toURI().toURL());
        }
        return urls;
    }

}
