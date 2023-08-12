package net.serenitybdd.model.history;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;

class ClearDirectoryContents implements PrepareHistoryDirectory {

    @Override
    public void prepareHistoryDirectory(Path historyDirectory) throws IOException {
        if (historyDirectory.toFile().exists()) {
            FileUtils.cleanDirectory(historyDirectory.toFile());
        }
    }
}
