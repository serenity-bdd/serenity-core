package net.thucydides.core.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VersionProviderTest {

    @Test
    public void getVersionNameFromArchiveName()
    {
        VersionProvider versionProvider = new VersionProvider();
        assertEquals("2.0.41", versionProvider.getVersionNameFromArchiveName("/test/home/serenity-core-2.0.41.jar"));
    }

    @Test
    public void getVersionNameFromSnapshotArchiveName()
    {
        VersionProvider versionProvider = new VersionProvider();
        assertEquals("2.0.41-SNAPSHOT", versionProvider.getVersionNameFromArchiveName("/test/home/serenity-model-2.0.41-SNAPSHOT.jar"));
    }
}
