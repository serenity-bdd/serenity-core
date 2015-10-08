package net.serenitybdd.core.photography;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import java.nio.file.FileSystem;

public class DarkroomFileSystem {
    private static ThreadLocal<FileSystem> fileSystemThreadLocal = new ThreadLocal<>();

    public static FileSystem get() {
        if (fileSystemThreadLocal.get() == null) {
            fileSystemThreadLocal.set(Jimfs.newFileSystem(Configuration.unix()));
        }
        return fileSystemThreadLocal.get();
    }

    public static void close() {
        fileSystemThreadLocal.remove();
    }
}
