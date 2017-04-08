package net.serenitybdd.core.history;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;

class ClearDirectoryContents implements PrepareHistoryDirectory {

    @Override
    public void prepareHistoryDirectory(Path historyDirectory) throws IOException {
        FileUtils.cleanDirectory(historyDirectory.toFile());
    }
}