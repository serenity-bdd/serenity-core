package net.thucydides.core.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VersionProviderTest {

    @Test
    public void getVersionNameFromArchiveName() throws Exception
    {
        VersionProvider versionProvider = new VersionProvider();
        ClassLoader classLoader = getClass().getClassLoader();
        assertEquals("2.0.19", versionProvider.getVersionNameFromCodeSourceURL(classLoader.getResource("util/serenity-model-2.0.19.jar")));
    }

    @Test
    public void getVersionNameFromSnapshotArchiveName() throws Exception
    {
        VersionProvider versionProvider = new VersionProvider();
        ClassLoader classLoader = getClass().getClassLoader();
        assertEquals("2.0.19-SNAPSHOT", versionProvider.getVersionNameFromCodeSourceURL(classLoader.getResource("util/serenity-model-2.0.19-SNAPSHOT.jar")));
    }

}
