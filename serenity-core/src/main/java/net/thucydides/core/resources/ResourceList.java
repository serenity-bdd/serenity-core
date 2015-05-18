package net.thucydides.core.resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
/**
 * Utility class to read report resources from the classpath. This way, report
 * resources such as images and stylesheets can be shipped in a separate JAR
 * file.
 */
public class ResourceList {

    private static final List<String> UNREQUIRED_FILES = Arrays.asList("pom.xml");
    private static final String PATH_SEPARATOR = System.getProperty("path.separator");

    private final Pattern pattern;

    public static ResourceList forResources(final Pattern pattern) {
        return new ResourceList(pattern);
    }

    protected ResourceList(final Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Find a list of resources matching a given path on the classpath. for all
     * elements of java.class.path get a Collection of resources Pattern pattern
     * = Pattern.compile(".*"); gets all resources
     * 
     * @return the resources in the order they are found
     */
    public Collection<String> list() {
        final ArrayList<String> resources = new ArrayList<String>();
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
            resources.addAll(getResources(classPathElement.getFile(), pattern));
        }
    }


    public Collection<String> systemPropertiesClasspathElements() {
        final ArrayList<String> resources = new ArrayList<String>();
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(PATH_SEPARATOR);
        for (final String element : classPathElements) {
            resources.addAll(getResources(element, pattern));
        }
        return resources;
    }


    private Collection<String> getResources(final String element, final Pattern pattern) {
        final ArrayList<String> resources = new ArrayList<String>();
        final File file = new File(element);
        if (isAJarFile(file)) {
            resources.addAll(getResourcesFromJarFile(file, pattern));
        } else {
            resources.addAll(getResourcesFromDirectory(file, pattern));
        }
        return removeUnnecessaryFilesFrom(resources);
    }

    private Collection<String> removeUnnecessaryFilesFrom(final Collection<String> resources) {
        final Collection<String> cleanedResources = new ArrayList<String>();
        for (String filepath : resources) {
            String filename = new File(filepath).getName();
            if (!UNREQUIRED_FILES.contains(filename)) {
                cleanedResources.add(filepath);
            }
        }
        return cleanedResources;
    }

    private boolean isAJarFile(final File file) {
        if (file.isDirectory()) {
            return false;
        } else {
            return (file.getName().endsWith(".jar"));
        }
    }

    protected ZipFile zipFileFor(final File file) throws IOException {
        return new ZipFile(file);
    }

    private Collection<String> getResourcesFromJarFile(final File file, final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        if (file.exists()) {
            ZipFile zf;
            try {
                zf = zipFileFor(file);
            } catch (final IOException e) {
                throw new ResourceCopyingError("Could not read from the JAR file", e);
            }
            @SuppressWarnings("rawtypes")
            final Enumeration e = zf.entries();
            while (e.hasMoreElements()) {
                final ZipEntry ze = (ZipEntry) e.nextElement();
                final String fileName = ze.getName();
                
                final boolean accept = pattern.matcher(fileName).matches();
                if (accept) {
                    retval.add(fileName);
                }
            }
            try {
                zf.close();
            } catch (final IOException e1) {
                throw new ResourceCopyingError("Couldn't close the zip file", e1);
            }
        }
        return retval;
    }

    private Collection<String> getResourcesFromDirectory(final File directory,
                                                                final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (final File file : fileList) {
                if (file.isDirectory() && (file.exists())) {
                    retval.addAll(getResourcesFromDirectory(file, pattern));
                } else {
                    if (file.exists()) {
                        try {
                            final String fileName = file.getCanonicalPath();
                            final boolean accept = pattern.matcher(fileName).matches();
                            if (accept) {
                                retval.add(fileName);
                            }
                        } catch (final IOException e) {
                            throw new ResourceCopyingError("Could not read from the JAR file", e);
                        }
                    }
                }
            }
        }
        return retval;
    }

}
