package org.wkhtmltopdf;

import com.sun.jna.Native;
import com.sun.jna.Platform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class WkHtmlToXLibraryLoader {

    private static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"), "org.wkhtmltopdf");

    private static class WkHtmlToXHolder {
        static final WkHtmlToX INSTANCE = loadLibraryInstance();
    }

    public static WkHtmlToX getInstance() {
        return WkHtmlToXHolder.INSTANCE;
    }

    private static WkHtmlToX loadLibraryInstance() {
        File libraryFile = loadLibraryFile();
        WkHtmlToX instance = Native.load(libraryFile.getAbsolutePath(), WkHtmlToX.class);
        return instance;
    }

    private static File loadLibraryFile() {
        if (!TEMP_DIR.exists() && !TEMP_DIR.mkdirs()) {
            throw new IllegalStateException("Unable to create wkhtmltox temporary directory");
        }
        if (!TEMP_DIR.canWrite()) {
            throw new IllegalStateException("Wkhtmltox temporary directory is not writable");
        }

        File libraryFile = new File(TEMP_DIR, getLibraryResourcePath());
        if (!libraryFile.exists()) {
            try {
                File dirPath = libraryFile.getParentFile();
                if (!dirPath.exists() && !dirPath.mkdirs()) {
                    throw new IllegalStateException("Unable to create directories for native library");
                }
                try (InputStream in = WkHtmlToXLibraryLoader.class.getResourceAsStream(getLibraryResourcePath())) {
                    Files.copy(in, libraryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load library resource", e);
            }
        }
        return libraryFile;
    }

    private static String getLibraryResourcePath() {
        StringBuilder resourcePath = new StringBuilder("/wkhtmltox/0.12.5/");
        if (!Platform.isWindows()) {
            resourcePath.append("lib");
        }
        resourcePath.append("wkhtmltox");
        if (!Platform.is64Bit()) {
            resourcePath.append(".32");
        }
        if (Platform.isWindows()) {
            resourcePath.append(".dll");
        } else if (Platform.isMac()) {
            resourcePath.append(".dylib");
        } else {
            resourcePath.append(".so");
        }
        return resourcePath.toString();
    }

}
