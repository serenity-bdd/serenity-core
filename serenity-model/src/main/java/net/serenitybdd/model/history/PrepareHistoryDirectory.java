package net.serenitybdd.model.history;

import java.io.IOException;
import java.nio.file.Path;

interface PrepareHistoryDirectory {
        void prepareHistoryDirectory(Path historyDirectory) throws IOException;
    }
