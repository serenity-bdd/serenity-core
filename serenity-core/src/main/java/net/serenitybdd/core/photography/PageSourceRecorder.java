package net.serenitybdd.core.photography;


import net.thucydides.core.webdriver.WebDriverFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class PageSourceRecorder {
    private final WebDriver driver;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public PageSourceRecorder(WebDriver driver) {
        this.driver = driver;
    }

    public Optional<File> intoDirectory(Path path) {
        byte[] pageSource = render(getPageSource());


        if (WebDriverFactory.isAlive(driver) && (pageSource.length > 0)) {
            try {
                Path pageSourceFile = Files.createTempFile(path, "pagesource", ".html");
                Files.write(pageSourceFile, pageSource);
                return Optional.of(pageSourceFile.toFile());
            } catch (IOException couldNotCreatePageSourcce) {
                LOGGER.warn("Could not save the page source HTML file", couldNotCreatePageSourcce);
            }
        }
        return Optional.empty();
    }


    private final static String HTML_PRISM_HIGHLIGHT = "<html lang='en'><head><link href='prism/prism.css' rel='stylesheet' /></head><body><script src='prism/prism.js'></script><body><div><pre><code class='language-html'>";
    private final static String HTML_CLOSE = "</code></pre></div></body></html>";

    private byte[] render(byte[] pageSource) {
        Document document = Jsoup.parse(new String(pageSource));
        String prettyHtml = document.html();
        String pageBody = Arrays.stream(prettyHtml.split("\\n"))
                .map(line -> line.replace("<", "&lt;").replace(">", "&gt;"))
                .collect(Collectors.joining("<br/>" + System.lineSeparator()));

        String renderedPage = HTML_PRISM_HIGHLIGHT + pageBody + HTML_CLOSE;

        return renderedPage.getBytes();
    }

    private byte[] getPageSource() {
        try {
            String ps = driver.getPageSource();
            if (ps == null) {
                return new byte[]{};
            }
            return ps.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.warn("Failed to get page source", e);
            return new byte[]{};
        }
    }
}
