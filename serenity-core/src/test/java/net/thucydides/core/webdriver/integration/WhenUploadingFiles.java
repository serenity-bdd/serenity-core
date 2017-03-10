package net.thucydides.core.webdriver.integration;

import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.components.FileToUpload;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
        driver = new ChromeDriver();
        pageFactory = new Pages(driver);
        openStaticTestSite(driver);
    }

    @AfterClass
    public static void closeBrowser() {
        if (driver != null) {
            driver.close();
            driver.quit();
        }
    }

    private static void openStaticTestSite(WebDriver driver) {
        File baseDir = new File(System.getProperty("user.dir"));
        File testSite = new File(baseDir,"src/test/resources/static-site/index.html");
        driver.get("file://" + testSite.getAbsolutePath());
    }


    @Test
    public void should_upload_a_file_from_the_resources_directory() {
        UploadPage uploadPage = pageFactory.get(UploadPage.class);

        uploadPage.uploadFile("uploads/readme.txt");

        assertThat(uploadPage.uploadField.getAttribute("value"), containsString("readme.txt"));

    }

    @Test
    public void should_upload_a_file_from_the_classpath() {
        UploadPage uploadPage = pageFactory.get(UploadPage.class);
        uploadPage.uploadFile("/report-resources/css/core.css");
        assertThat(uploadPage.uploadField.getAttribute("value"), containsString("core.css"));
    }

    @Test
    public void should_upload_a_file_from_the_classpath_on_the_local_mahcine() {
        UploadPage uploadPage = pageFactory.get(UploadPage.class);
        uploadPage.uploadFileFromLocal("/report-resources/css/core.css");
        assertThat(uploadPage.uploadField.getAttribute("value"), containsString("core.css"));
    }

    @Test
    public void should_upload_a_file_data_in_string_form() throws IOException, URISyntaxException {
        UploadPage uploadPage = pageFactory.get(UploadPage.class);

        uploadPage.uploadFileData("data data data");

        assertThat(uploadPage.uploadField.getAttribute("value"), not(isEmptyString()));
    }

    @Test
    public void should_upload_a_byte_array() throws IOException {
        UploadPage uploadPage = pageFactory.get(UploadPage.class);

        uploadPage.uploadFileData("data data data".getBytes());

        assertThat(uploadPage.uploadField.getAttribute("value"), not(isEmptyString()));

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
        assertThat(FileToUpload.isAFullWindowsPath("C:\\Projects\\somefile.pdf"), is(true));
    }

    @Test
    public void should_recognize_a_unix_path() {
        assertThat(FileToUpload.isAFullWindowsPath("/home/john/somefile.pdf"), is(false));
    }

    @Test
    public void should_recognize_a_complex_unix_path() {
        assertThat(FileToUpload.isAFullWindowsPath("/home/myuser/target/test-classes/documentUpload/somefile.pdf"), is(false));
    }

    @Test
    public void should_upload_a_relative_path_from_the_current_working_directory() throws IOException {

        Path tempPath = Files.createTempDirectory("temp");
        File targetDirectory = tempPath.toFile();
        File uploadedFile = new File(targetDirectory, "upload.txt");
        writeTextToFile(uploadedFile);

        UploadPage uploadPage = pageFactory.get(UploadPage.class);

        uploadPage.uploadFile("target/upload.txt");

        assertThat(uploadPage.uploadField.getAttribute("value"), containsString("upload.txt"));
    }

    private void writeTextToFile(File uploadedFile) throws IOException {
        PrintWriter out = new PrintWriter(uploadedFile);
        out.close();
    }

}
