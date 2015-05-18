package net.thucydides.core.util;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ExtendedTemporaryFolderTest {

	@Rule
    public ExtendedTemporaryFolder temporaryFolder = new ExtendedTemporaryFolder();

	@Test
	public void newFolderTest() throws IOException {
		for (int i = 0; i < 1000; i++) {
			File temporary = this.temporaryFolder.newFolder();
			Assert.assertEquals(true, temporary.exists());

			// Should writable
			Assert.assertEquals(true, temporary.canWrite());

			// Should writable
			Assert.assertEquals(true, temporary.canRead());
		}
	}

	@Test
	public void newFileTest() throws IOException {
		for (int i = 0; i < 100; i++) {
			File temporary = this.temporaryFolder.newFile(new String(String.valueOf(i)));
			Assert.assertEquals(true, temporary.exists());
	
			// Should writable
			Assert.assertEquals(true, temporary.canWrite());

			// Should writable
			Assert.assertEquals(true, temporary.canRead());
		}
	}
}
