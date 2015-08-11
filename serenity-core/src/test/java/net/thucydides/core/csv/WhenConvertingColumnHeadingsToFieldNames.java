package net.thucydides.core.csv;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(Parameterized.class)
public class WhenConvertingColumnHeadingsToFieldNames {
    private String columnName;
    private String expectedFieldName;

    public WhenConvertingColumnHeadingsToFieldNames(final String columnName,
                                                    final String expectedFieldName) {
        this.columnName = columnName;
        this.expectedFieldName = expectedFieldName;
    }

    @Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]
                {{"phone", "phone"},
                {"Phone", "phone"},
                {"  Phone  ", "phone"},
                {"PHONE", "phone"},
                {"Date Of Birth", "dateOfBirth"},
                {"DATE OF BIRTH", "dateOfBirth"},
                {"  DATE OF BIRTH  ", "dateOfBirth"},
                {"date of birth", "dateOfBirth"}};
        return Arrays.asList(data);
    }

    @Test
    public void should_convert_column_name_to_javabean_property_name() {
        String normalizedName = FieldName.from(columnName).inNormalizedForm();
        assertThat(normalizedName, is(expectedFieldName));

    }


}
