package net.serenitybdd.core.photography;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import java.nio.file.FileSystem;

public class DarkroomFileSystem {
    private static ThreadLocal<FileSystem> fileSystemThreadLocal = new ThreadLocal<FileSystem>() {
        @Override
        protected FileSystem initialValue() {
            return Jimfs.newFileSystem(Configuration.unix());
        }
    };

    public static FileSystem get() {
        return fileSystemThreadLocal.get();
    }

}
