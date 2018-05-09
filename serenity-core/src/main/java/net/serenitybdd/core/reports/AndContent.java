package net.serenitybdd.core.reports;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

public interface AndContent {
    void andContents(String contents);
    void fromFile(Path source) throws IOException;
    void fromFile(Path source, Charset encoding) throws IOException;
}