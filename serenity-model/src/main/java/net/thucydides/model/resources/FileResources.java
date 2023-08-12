package net.thucydides.model.resources;

import net.serenitybdd.model.SerenitySystemProperties;
import net.thucydides.model.ThucydidesSystemProperty;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * Utility class used to copy resources from a classpath to a target directory.
 */
public class FileResources {

    private static final int BUFFER_SIZE = 4096;
    private static final int DEFAULT_FILE_IO_RETRY_TIMEOUT = 60;

    private String resourceDirectoryRoot;

    public static FileResources from(final String resourceDirectoryRoot) {
        return new FileResources(resourceDirectoryRoot);
    }

    protected FileResources(final String resourceDirectoryRoot) {
        this.resourceDirectoryRoot = resourceDirectoryRoot;
    }

    public final String findTargetSubdirectoryFrom(final String sourceResource) {
        int directoryRootStartsAt = StringUtils.lastIndexOf(sourceResource,
                resourceDirectoryRoot);
        int relativePathStartsAt = directoryRootStartsAt
                + resourceDirectoryRoot.length() + 1;
        String relativePath = sourceResource.substring(relativePathStartsAt);
        relativePath = stripLeadingSeparatorFrom(relativePath);
        return directoryIn(relativePath);
    }

    public final String stripLeadingSeparatorFrom(final String path) {
        if (path.startsWith("/") || path.startsWith("\\")) {
            return path.substring(1);
        } else {
            return path;
        }
    }

    public final String findTargetFileFrom(final String sourceResource) {
        int directoryRootStartsAt = StringUtils.lastIndexOf(sourceResource,
                resourceDirectoryRoot);
        int relativePathStartsAt = directoryRootStartsAt
                + resourceDirectoryRoot.length() + 1;
        String relativePath = sourceResource.substring(relativePathStartsAt);

        return filenameIn(relativePath);
    }

    public final void copyResourceTo(final String sourceResource, final File targetDirectory)
            throws IOException {

        String targetFile = findTargetFileFrom(sourceResource);
        String targetRelativeDirectory = findTargetSubdirectoryFrom(sourceResource);

        File destinationDirectory = targetDirectory;

        if (targetRelativeDirectory.length() > 0) {
            destinationDirectory = new File(targetDirectory, targetRelativeDirectory);
        }

        if (new File(sourceResource).isDirectory()) {
            File fullTargetDirectory = new File(destinationDirectory, targetFile);
            fullTargetDirectory.mkdirs();
        } else {
            copyFileFromClasspathToTargetDirectory(sourceResource, destinationDirectory);
        }
    }

    private void copyFileFromClasspathToTargetDirectory(
            final String resourcePath, final File targetDirectory)
            throws IOException {
        File destinationFile = new File(targetDirectory, new File(resourcePath).getName());
        if (destinationFile.exists()) {
            return;
        }
        if (destinationFile.getParent() != null) {
            new File(destinationFile.getParent()).mkdirs();
        }
        try(InputStream in = fileSource(resourcePath);
            FileOutputStream out = getOutputStreamForDestination(destinationFile);) {
            copyData(in, out);
        }
    }

    private InputStream fileSource(final String resourcePath) throws FileNotFoundException{
        InputStream source = null;
        File resourceOnClasspath = new File(resourcePath);
        if (resourceOnClasspath.exists()) {
            source = new FileInputStream(resourceOnClasspath);
        } else {
            source = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        }
        return source;
    }

	private FileOutputStream getOutputStreamForDestination(File destinationFile) throws FileNotFoundException {
		FileOutputStream outStream = null;
		long start = new java.util.Date().getTime();
		long timeout = getRetryTimeOut();
		long timeElapsed = 0;
		boolean FILE_NOT_FOUND = true;
		while (FILE_NOT_FOUND) {
			try{
				timeElapsed = new java.util.Date().getTime() - start;
				outStream = createOutputStream(destinationFile);
                FILE_NOT_FOUND = false;
			}catch(FileNotFoundException fnfe) {
				if (timeElapsed > timeout) {
					//timeout
					throw fnfe;
				}
			}
		}

		return outStream;
	}

    private long getRetryTimeOut() {

        SerenitySystemProperties systemProperties = SerenitySystemProperties.getProperties();
        int timeout = systemProperties.getIntegerValue(ThucydidesSystemProperty.SERENITY_FILE_IO_RETRY_TIMEOUT, DEFAULT_FILE_IO_RETRY_TIMEOUT);
        return timeout * 1000; //milliseconds
    }

    public static long getDefaultRetryTimeout() {
        return DEFAULT_FILE_IO_RETRY_TIMEOUT * 1000;
    }

    protected FileOutputStream createOutputStream(File destinationFile) throws FileNotFoundException {
        return new FileOutputStream(destinationFile);
    }

    private void copyData(final InputStream in, final OutputStream out)
            throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((in != null) && (bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }

    private String directoryIn(final String path) {

        if (path.contains("/")) {
            int filenameStartsAt = StringUtils.lastIndexOf(path,"/");
            return path.substring(0, filenameStartsAt);

        } else if (path.contains("\\")) {
            int filenameStartsAt = StringUtils.lastIndexOf(path,"\\");
            return path.substring(0, filenameStartsAt);
        } else {
            return "";
        }
    }

    private String filenameIn(final String path) {

        if (path.contains("/")) {
            int filenameStartsAt = StringUtils.lastIndexOf(path,"/");
            return path.substring(filenameStartsAt + 1);

        } else if (path.contains("\\")) {
            int filenameStartsAt = StringUtils.lastIndexOf(path,"\\");
            return path.substring(filenameStartsAt + 1);
        } else {
            return path;
        }
    }
}
