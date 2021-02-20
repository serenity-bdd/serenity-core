package net.serenitybdd.cli.reporters;

import java.io.IOException;
import java.nio.file.Path;

public interface CLIReportGenerator {
    void generateReportsFrom(Path sourceDirectory) throws IOException;
}
