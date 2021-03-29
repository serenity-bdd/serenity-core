package serenitycore.net.serenitybdd.core.photography;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.FileSystem;

public class DarkroomFileSystem {
    private static ThreadLocal<FileSystem> fileSystemThreadLocal = new ThreadLocal<>();

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

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
