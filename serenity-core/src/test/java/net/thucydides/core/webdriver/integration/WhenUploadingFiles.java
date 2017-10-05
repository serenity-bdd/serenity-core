package net.thucydides.core.webdriver.integration;

import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.components.FileToUpload;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WhenUploadingFiles {

    public static class UploadPage extends PageObject {

        @FindBy(name = "upload")
        public WebElement uploadField;

        public UploadPage(WebDriver driver) {
            super(driver);
        }

        public void uploadFile(String filename) {
            upload(filename).to(uploadField);
        }

        public void uploadFileFromLocal(String filename) {
            upload(filename).fromLocalMachine().to(uploadField);
        }

        public void uploadFileFromClasspath(String filename) {
            upload(filename).fromClasspath().to(uploadField);
        }


        public void uploadFileData(String data) throws IOException {
            uploadData(data).to(uploadField);
        }

        public void uploadFileData(byte[] data) throws IOException {
            uploadData(data).to(uploadField);
        }

    }

    private static WebDriver driver;
    private static Pages pageFactory;

    @BeforeClass
    public static void open_local_static_site() {
        driver = new FirefoxDriver();//ChromeDriver();
        pageFactory = new Pages(driver);
        openStaticTestSite(driver);
    }

    @Before
    public void refreshScreen() {
        driver.navigate().refresh();
    }

    @AfterClass
    public static void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private static void openStaticTestSite(WebDriver driver) {
        File baseDir = new File(System.getProperty("user.dir"));
        File testSite = new File(baseDir,"src/test/resources/static-site/index.html");
        driver.get("file://" + testSite.getAbsolutePath());
    }


    @Test
    public void should_upload_unqualified_filenames_from_the_src_test_resources_directory() {
        UploadPage uploadPage = pageFactory.get(UploadPage.class);

        uploadPage.uploadFile("uploads/readme.txt");

        assertThat(uploadPage.uploadField.getAttribute("value")).contains("readme.txt");
    }

    @Test
    public void should_fail_with_a_sensible_error_if_the_file_doesn_not_exist() {
        UploadPage uploadPage = pageFactory.get(UploadPage.class);

        Throwable thrown = catchThrowable(() -> { uploadPage.uploadFile("uploads/does_not_exist.txt"); });

        assertThat(thrown).isInstanceOf(InvalidArgumentException.class)
                          .hasMessageStartingWith("File not found:")
                          .hasMessageContaining("does_not_exist.txt");
    }

    @Test
    public void should_upload_a_file_from_the_classpath() {
        UploadPage uploadPage = pageFactory.get(UploadPage.class);
        uploadPage.uploadFileFromClasspath("report-resources/css/core.css");
        assertThat(uploadPage.uploadField.getAttribute("value")).contains("core.css");
    }

    @Test
    public void should_fail_with_a_sensible_error_if_the_classpath_file_doesn_not_exist() {
        UploadPage uploadPage = pageFactory.get(UploadPage.class);

        Throwable thrown = catchThrowable(() -> { uploadPage.uploadFileFromClasspath("report-resources/css/does_not_exist.txt"); });

        assertThat(thrown).isInstanceOf(InvalidArgumentException.class)
                .hasMessageStartingWith("File not found on classpath:")
                .hasMessageContaining("does_not_exist.txt");
    }

    @Test
    public void should_upload_a_file_from_the_classpath_on_the_local_machine() {
        UploadPage uploadPage = pageFactory.get(UploadPage.class);
        uploadPage.uploadFileFromLocal("/report-resources/css/core.css");
        assertThat(uploadPage.uploadField.getAttribute("value")).contains("core.css");
    }

    @Test
    public void should_upload_a_file_data_in_string_form() throws IOException, URISyntaxException {
        UploadPage uploadPage = pageFactory.get(UploadPage.class);

        uploadPage.uploadFileData("data data data");

        assertThat(uploadPage.uploadField.getAttribute("value")).isNotBlank();
    }

    @Test
    public void should_upload_a_byte_array() throws IOException {
        UploadPage uploadPage = pageFactory.get(UploadPage.class);

        uploadPage.uploadFileData("data data data".getBytes());

        assertThat(uploadPage.uploadField.getAttribute("value")).isNotBlank();

    }

    @Test
    public void should_leave_a_unix_java_path_alone_when_running_on_unix() {
        String unixPath = "/home/myuser/target/test-classes/documentUpload/somefile.pdf";
        if (!runningOnWindows()) {
            WebElement field = mock(WebElement.class);

            FileToUpload fileToUpload = new FileToUpload(driver, unixPath);
            fileToUpload.to(field);

            verify(field).sendKeys(unixPath);
        }
    }

    @Test
    public void should_leave_a_windows_java_path_alone_when_running_on_windows() {
        String windowsPath = "C:\\Users\\Joe Blogs\\Documents\\somefile.pdf";
        if (runningOnWindows()) {
            WebElement field = mock(WebElement.class);

            FileToUpload fileToUpload = new FileToUpload(driver, windowsPath);
            fileToUpload.to(field);

            verify(field).sendKeys(windowsPath);
        }
    }

    private boolean runningOnWindows() {
        return System.getProperty("os.name").contains("Windows");
    }

    @Test
    public void should_recognize_a_simple_windows_path() {
        assertThat(FileToUpload.isAFullWindowsPath("C:\\Projects\\somefile.pdf")).isTrue();
    }

    @Test
    public void should_recognize_a_unix_path() {
        assertThat(FileToUpload.isAFullWindowsPath("/home/john/somefile.pdf")).isFalse();
    }

    @Test
    public void should_recognize_a_complex_unix_path() {
        assertThat(FileToUpload.isAFullWindowsPath("/home/myuser/target/test-classes/documentUpload/somefile.pdf")).isFalse();
    }

    private void writeTextToFile(File uploadedFile) throws IOException {
        PrintWriter out = new PrintWriter(uploadedFile);
        out.close();
    }

}
