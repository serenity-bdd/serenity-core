package net.thucydides.core.reports.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class HtmlMinifyingWriter extends Writer {

    public static Writer minifyingWriter(Path outputPath) throws IOException {
        BufferedWriter baseWriter = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8);
        if (outputPath.toString().endsWith(".html")) {
            return new HtmlMinifyingWriter(baseWriter);
        }
        return baseWriter;
    }

    private final Writer delegate;
    private final StringBuilder buffer = new StringBuilder();

    public HtmlMinifyingWriter(Writer delegate) {
        this.delegate = delegate;
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        buffer.append(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        compressAndWrite();
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        compressAndWrite();
        delegate.close();
    }

    private void compressAndWrite() throws IOException {
        String content = buffer.toString();

        // Basic HTML whitespace compression
        String minified = content
            .replaceAll(">\\s+<", "><")         // remove whitespace between tags
            .replaceAll("\\s{2,}", " ")         // collapse multiple spaces
            .replaceAll("^\\s+|\\s+$", "");     // trim leading/trailing whitespace

        delegate.write(minified);
    }
}
