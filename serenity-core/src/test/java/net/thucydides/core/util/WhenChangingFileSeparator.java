package net.thucydides.core.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenChangingFileSeparator {

	@Test
	public void front_slashes_are_converted_to_system_specific_separator() {
		String[] randomFileNameNodes = {"target", "site","thucydides","datatables"};
		String originalFileName = StringUtils.join(randomFileNameNodes, "/");
		String expectedFileName = StringUtils.join(randomFileNameNodes, File.separator);
		
		assertThat(FileSeparatorUtil.changeSeparatorIfRequired(originalFileName), is(expectedFileName));
	}

    @Test
    public void back_slashes_are_converted_to_system_specific_separator() {
        String[] randomFileNameNodes = {"target", "site","thucydides","datatables"};
        String systemSpecificSeparator = System.getProperty("file.separator");
        String originalFileName = StringUtils.join(randomFileNameNodes, systemSpecificSeparator);
        String expectedFileName = StringUtils.join(randomFileNameNodes, File.separator);

        assertThat(FileSeparatorUtil.changeSeparatorIfRequired(originalFileName), is(expectedFileName));
    }
	
}
