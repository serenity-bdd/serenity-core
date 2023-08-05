package net.thucydides.model.resources;

import net.serenitybdd.model.collect.NewList;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
/**
 * Utility class to read report resources from the classpath. This way, report
 * resources such as images and stylesheets can be shipped in a separate JAR
 * file.
 */
public class ResourceList {

    private static final List<String> UNREQUIRED_FILES = NewList.of("pom.xml");
    private static final String PATH_SEPARATOR = System.getProperty("path.separator");

    final Pattern pattern;
    final String resourceDirectory;

    private static final long TO_GIVE_THE_OTHER_PROCESS_TIME_TO_RELEASE_THE_ZIP = 500;

    protected ResourceList(String resourceDirectory, Pattern pattern) {

        this.pattern = pattern;
        this.resourceDirectory = resourceDirectory;
    }

    public static ResourceList forResources(final String resourceDirectory, final Pattern pattern) {
        return new ResourceList(resourceDirectory, pattern);
    }

    /**
     * Find a list of resources matching a given path on the classpath. for all
     * elements of java.class.path get a Collection of resources Pattern pattern
     * = Pattern.compile(".*"); gets all resources
     * 
     * @return the resources in the order they are found
     */
    public Collection<String> list() {
        final ArrayList<String> resources = new ArrayList<>();
        resources.addAll(systemPropertiesClasspathElements());

        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            addResourcesFromUrlClassLoader(resources, (URLClassLoader) classLoader);
        }

        return resources;
    }

    private void addResourcesFromUrlClassLoader(ArrayList<String> resources, URLClassLoader classLoader) {
        URL[] classPathElements = classLoader.getURLs();
        for(URL classPathElement : classPathElements) {
            if (isASerenityResourceJarFile(classPathElement.getFile())) {
                resources.addAll(getResources(classPathElement.getFile(), pattern));
            }
        }
    }

    private boolean isASerenityResourceJarFile(String file) {
        return file.contains("serenity-core") || file.contains("serenity-report-resources") ;
    }

    private Collection<String> systemPropertiesClasspathElements() {
        final ArrayList<String> resources = new ArrayList<>();
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(PATH_SEPARATOR);
        for (final String element : classPathElements) {
            resources.addAll(getResources(element, pattern));
        }
        return resources;
    }


    private Collection<String> getResources(final String element, final Pattern pattern) {
        final ArrayList<String> resources = new ArrayList<>();
        final File file = new File(element);
        if (isAJarFile(file)) {
            resources.addAll(getResourcesFromJarFile(file, pattern));
        } else {
            resources.addAll(getResourcesFromDirectory(file, pattern));
        }
        return removeUnnecessaryFilesFrom(resources);
    }

    private Collection<String> removeUnnecessaryFilesFrom(final Collection<String> resources) {
        final Collection<String> cleanedResources = new ArrayList<>();
        for (String filepath : resources) {
            String filename = new File(filepath).getName();
            if (!UNREQUIRED_FILES.contains(filename)) {
                cleanedResources.add(filepath);
            }
        }
        return cleanedResources;
    }

    private boolean isAJarFile(final File file) {
        return !file.isDirectory() && (file.getName().endsWith(".jar"));
    }

    protected ZipFile zipFileFor(final File file) throws IOException {
        return loadZipFileWithMaxRetries(file, 3);
    }

    private ZipFile loadZipFileWithMaxRetries(final File file, int maxRetries) throws IOException {
        try {
            return new ZipFile(file);
        } catch (ZipException resourceFileLockedOrSomething) {
            if (maxRetries == 0) {
                throw resourceFileLockedOrSomething;
            }
            try {
                Thread.sleep(TO_GIVE_THE_OTHER_PROCESS_TIME_TO_RELEASE_THE_ZIP);
            } catch (InterruptedException shouldNeverHappen) {
                throw new RuntimeException(shouldNeverHappen);
            }
            return loadZipFileWithMaxRetries(file, maxRetries - 1);
        }
    }

    private Collection<String> getResourcesFromJarFile(final File file, final Pattern pattern) {
        final ArrayList<String> matchingResources = new ArrayList<>();
        try{
            if (file.exists()) {
                try (ZipFile zf = zipFileFor(file)) {
                    @SuppressWarnings("rawtypes")
                    final Enumeration e = zf.entries();
                    while (e.hasMoreElements()) {
                        final ZipEntry entry = (ZipEntry) e.nextElement();
                        final String fileName = entry.getName();

                        if (resourceDirectory.isEmpty() || fileName.contains(resourceDirectory)) {
                            if (pattern.matcher(fileName).matches()) {
                                matchingResources.add(fileName);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ResourceCopyingError("Couldn't close the zip file " + file, e);
        }
        return matchingResources;
    }

    private Collection<String> getResourcesFromDirectory(final File directory, final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<>();
        final File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (final File file : fileList) {
                if (file.isDirectory() && (file.exists())) {
                    retval.addAll(getResourcesFromDirectory(file, pattern));
                } else {
                    String fileName = "";
                    if (file.exists()) {
                        try {
                            fileName = file.getCanonicalPath();
                            final boolean accept = pattern.matcher(fileName).matches();
                            if (accept) {
                                retval.add(fileName);
                            }
                        } catch (final IOException e) {
                            throw new ResourceCopyingError("Could not read from the JAR file " + fileName , e);
                        }
                    }
                }
            }
        }
        return retval;
    }

}
