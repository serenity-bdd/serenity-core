package net.thucydides.core.util;

import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ExtendedTemporaryFolder extends ExternalResource {
	
	private static final Logger logger = LoggerFactory.getLogger(ExtendedTemporaryFolder.class);

	private final File parentFolder;
    private File folder;

    public ExtendedTemporaryFolder() {
    	this(null);
	}

    public ExtendedTemporaryFolder(File parentFolder) {
        this.parentFolder = parentFolder;
    }

    @Override
    protected void before() throws Throwable {
        create();
    }

    @Override
    protected void after() {
        delete();
    }

    // testing purposes only

    /**
     * for testing purposes only. Do not use.
     */
    public void create() throws IOException {
        folder = createTemporaryFolderIn(parentFolder);
    }

    /**
     * @return a new fresh file with a random name under the temporary folder.
     */
    public File newFile() throws IOException {
        return File.createTempFile("junit", null, getRoot());
    }

    /**
     * @param  folder name of the new temporary directory
     * @return a new fresh folder with the given name under the temporary folder.
     */
    public File newFolder(String folder) throws IOException {
        return newFolder(new String[]{folder});
    }

    /**
     * @param  folderNames a sequence of folder names used to create a temporary directory
     * @return a new fresh folder with the given name(s) under the temporary folder.
     */
    public File newFolder(String... folderNames) throws IOException {
        File file = getRoot();
        for (int i = 0; i < folderNames.length; i++) {
            String folderName = folderNames[i];
            file = new File(file, folderName);
            if (!file.mkdir() && isLastElementInArray(i, folderNames)) {
                throw new IOException(
                        "a folder with the name \'" + folderName + "\' already exists");
            }
        }
        return file;
    }

    private boolean isLastElementInArray(int index, String[] array) {
        return index == array.length - 1;
    }

    private File createTemporaryFolderIn(File parentFolder) throws IOException {
        File createdFolder = File.createTempFile("junit", "", parentFolder);
        createdFolder.delete();
        createdFolder.mkdir();
        return createdFolder;
    }

    /**
     * @return the location of this temporary folder.
     */
    public File getRoot() {
        if (folder == null) {
            throw new IllegalStateException(
                    "the temporary folder has not yet been created");
        }
        return folder;
    }

    /**
     * Delete all files and folders under the temporary folder. Usually not
     * called directly, since it is automatically applied by the {@link Rule}
     */
    public void delete() {
        if (folder != null) {
            recursiveDelete(folder);
        }
    }

    private void recursiveDelete(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File each : files) {
                recursiveDelete(each);
            }
        }
        file.delete();
    }

	protected static synchronized boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}

	public File newFolder() throws IOException {
		if (isWindows()) {
			synchronized (this) {
				try {
					return createTemporaryFolderIn(getRoot());
				} catch (IOException e) {
					logger.debug("Error when invoke newFolder(): {}", e);
					throw e;
				}
			}
		} else {
			return createTemporaryFolderIn(getRoot());
		}
	}
	
	public File newFile(String fileName) throws IOException {
		if (isWindows()) {
			synchronized (this) {
				File file= new File(getRoot(), fileName);
                file.setWritable(true);
                file.setReadable(true);
                file.createNewFile();
				return file;
			}
		} else {
            File file = new File(getRoot(), fileName);
            file.createNewFile();
            return file;
		}
	}
}
