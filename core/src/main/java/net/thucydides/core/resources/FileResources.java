package net.thucydides.core.resources;

import net.thucydides.core.ThucydidesSystemProperties;
import net.thucydides.core.ThucydidesSystemProperty;
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

        FileOutputStream out = null;
        InputStream in = null;
        try {
            File resourceOnClasspath = new File(resourcePath);

            if (resourceOnClasspath.exists()) {
                in = new FileInputStream(resourceOnClasspath);
            } else {
                in = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
            }
            File destinationFile = new File(targetDirectory, resourceOnClasspath.getName());

            if (destinationFile.exists()) {
                return;
            }
            if (destinationFile.getParent() != null) {
                new File(destinationFile.getParent()).mkdirs();
            }

            out = getOutputStreamForDestination(destinationFile);

            copyData(in, out);
        } finally {
            closeSafely(out, in);
        }
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

        ThucydidesSystemProperties systemProperties = ThucydidesSystemProperties.getProperties();
        int timeout = systemProperties.getIntegerValue(ThucydidesSystemProperty.THUCYDIDES_FILE_IO_RETRY_TIMEOUT, DEFAULT_FILE_IO_RETRY_TIMEOUT);
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
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }

    private void closeSafely(final OutputStream out, final InputStream in)
            throws IOException {
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
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