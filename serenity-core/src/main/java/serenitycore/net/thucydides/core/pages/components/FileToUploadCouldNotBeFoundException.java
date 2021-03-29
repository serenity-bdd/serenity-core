package serenitycore.net.thucydides.core.pages.components;

public class FileToUploadCouldNotBeFoundException extends RuntimeException {
    public FileToUploadCouldNotBeFoundException(String filePath) {
        super("File not found: " + filePath);
    }
}
