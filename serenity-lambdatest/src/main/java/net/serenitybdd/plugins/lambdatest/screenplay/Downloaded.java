package net.serenitybdd.plugins.lambdatest.screenplay;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

/**
 * Questions about files downloaded on the LambdaTest server (see https://www.lambdatest.com/support/docs/download-files-using-lambdatest-selenium-grid/)
 */
public class Downloaded {

    /**
     * Check whether the downloaded file exists in the test machine.
     */
    public static Question<Boolean> fileExists(String filename) {
        return Question.about("the " + filename + "file was downloaded").answeredBy(
                actor -> (Boolean) BrowseTheWeb.as(actor).evaluateJavascript(lamdaFileExists(filename))
        );
    }

    /**
     * Retrieve file metadata such as md5 code, modified time, name and size
     */
    public static Question<Object> fileMetaData(String filename) {
        return Question.about("the " + filename + "file metadata").answeredBy(
                actor -> BrowseTheWeb.as(actor).evaluateJavascript(lamdaFileMetadata(filename))
        );
    }

    /**
     * Download file content using base64 encoding
     */
    public static Question<String> fileContent(String filename) {
        return Question.about("the " + filename + "file metadata").answeredBy(
                actor -> (String) BrowseTheWeb.as(actor).evaluateJavascript(lamdaFileContent(filename))
        );
    }

    private static String lamdaFileExists(String filename) {
        return "lambda-file-exists=" + filename;
    }

    private static String lamdaFileMetadata(String filename) {
        return "lambda-file-stats=" + filename;
    }

    private static String lamdaFileContent(String filename) {
        return "lambda-file-content=" + filename;
    }

}
