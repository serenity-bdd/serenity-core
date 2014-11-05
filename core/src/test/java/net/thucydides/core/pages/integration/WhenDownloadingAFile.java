package net.thucydides.core.pages.integration;

import net.thucydides.core.pages.components.FileToDownload;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by john on 30/10/2014.
 */
public class WhenDownloadingAFile {

    @Test
    public void should_download_url_as_byte_array() throws IOException {
        URL url = new URL("http://www.apache.org/licenses/LICENSE-2.0.html");
        byte[] data = FileToDownload.fromUrl(url).asByteArray();
        assertThat(data.length).isGreaterThan(0);
    }

    @Test
    public void should_download_url_as_string() throws IOException {
        URL url = new URL("http://www.apache.org/licenses/LICENSE-2.0.html");
        String contents = FileToDownload.fromUrl(url).asString();
        assertThat(contents).isNotEmpty();
    }

}
