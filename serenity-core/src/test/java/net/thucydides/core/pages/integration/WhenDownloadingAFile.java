package net.thucydides.core.pages.integration;

import net.thucydides.core.pages.components.FileToDownload;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

;

/**
 * Created by john on 30/10/2014.
 */
public class WhenDownloadingAFile {

    @Test
    public void should_download_url_as_byte_array() throws IOException {
        URL url = new URL("http://example.com");
        byte[] data = FileToDownload.fromUrl(url).asByteArray();
        assertThat(data.length).isGreaterThan(0);
    }

    @Test
    public void should_download_url_as_string() throws IOException {
        URL url = new URL("http://example.com");
        String contents = FileToDownload.fromUrl(url).asString();
        assertThat(contents).isNotEmpty();
    }

}
