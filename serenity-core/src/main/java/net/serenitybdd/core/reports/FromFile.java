package net.serenitybdd.core.reports;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

public interface FromFile {
    void fromFile(Path source) throws IOException;
    void fromFile(Path source, Charset encoding) throws IOException;
    FromFile downloadable();
}