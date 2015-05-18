package net.thucydides.core.pages.components;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by john on 30/10/2014.
 */
public class FileToDownload {
    private final URL url;

    public static FileToDownload fromUrl(URL url) {
        return new FileToDownload(url);
    }

    public FileToDownload(URL url) {
        this.url = url;
    }

    public byte[] asByteArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try(InputStream in = new BufferedInputStream(url.openStream())) {
            IOUtils.copy(in, out);
        }
        return out.toByteArray();
    }

    public String asString() throws IOException {
        return new String(asByteArray());
    }
}
