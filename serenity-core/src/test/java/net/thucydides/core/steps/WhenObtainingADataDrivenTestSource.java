package net.thucydides.core.steps;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenObtainingADataDrivenTestSource {

    @Test
    public void should_convert_data_file_path_to_operating_system_localized_path() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.data.dir","C:\\some\\dir");

        FilePathParser testDataSourcePath = new FilePathParser(environmentVariables) {
            @Override
            protected String getFileSeparator() {
                return "\\";
            }
        };

        assertThat(testDataSourcePath.getInstanciatedPath("${DATADIR}/simple-semicolon-data.csv"), is("C:\\some\\dir\\simple-semicolon-data.csv"));
    }


    @Test
    public void should_work_without_curly_brackets() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.data.dir","C:\\some\\dir");

        FilePathParser testDataSourcePath = new FilePathParser(environmentVariables) {
            @Override
            protected String getFileSeparator() {
                return "\\";
            }
        };

        assertThat(testDataSourcePath.getInstanciatedPath("$DATADIR/simple-semicolon-data.csv"), is("C:\\some\\dir\\simple-semicolon-data.csv"));
    }

    @Test
    public void should_work_with_environment_specific_config() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("environments.dev.serenity.data.dir","C:\\some\\dir");
        environmentVariables.setProperty("environment","dev");

        FilePathParser testDataSourcePath = new FilePathParser(environmentVariables) {
            @Override
            protected String getFileSeparator() {
                return "\\";
            }
        };

        assertThat(testDataSourcePath.getInstanciatedPath("$DATADIR/simple-semicolon-data.csv"), is("C:\\some\\dir\\simple-semicolon-data.csv"));
    }
    @Test
    public void should_convert_data_file_path_to_operating_system_localized_path_in_unix() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.data.dir","/some/dir");

        FilePathParser testDataSourcePath = new FilePathParser(environmentVariables) {
            @Override
            protected String getFileSeparator() {
                return "/";
            }
        };

        assertThat(testDataSourcePath.getInstanciatedPath("${DATADIR}/simple-semicolon-data.csv"), is("/some/dir/simple-semicolon-data.csv"));
    }

    @Test
    public void should_inject_user_home_directory_into_path() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("user.home","/home/john");
        FilePathParser testDataSourcePath = new FilePathParser(environmentVariables) {
            @Override
            protected String getFileSeparator() {
                return "/";
            }
        };
        assertThat(testDataSourcePath.getInstanciatedPath("${user.home}/simple-semicolon-data.csv"), is("/home/john/simple-semicolon-data.csv"));

    }

    @Test
    public void should_inject_user_home_directory_into_path_using_HOME_variable() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("user.home","/home/john");
        FilePathParser testDataSourcePath = new FilePathParser(environmentVariables) {
            @Override
            protected String getFileSeparator() {
                return "/";
            }
        };
        assertThat(testDataSourcePath.getInstanciatedPath("$HOME/simple-semicolon-data.csv"), is("/home/john/simple-semicolon-data.csv"));
    }

    @Test
    public void should_inject_current_directory_into_path() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("user.dir","/home/john/work");
        FilePathParser testDataSourcePath = new FilePathParser(environmentVariables) {
            @Override
            protected String getFileSeparator() {
                return "/";
            }
        };
        assertThat(testDataSourcePath.getInstanciatedPath("${user.dir}/simple-semicolon-data.csv"), is("/home/john/work/simple-semicolon-data.csv"));

    }

    @Test
    public void should_inject_current_directory_into_path_using_USERDIR_variable() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("user.dir","/home/john/work");
        FilePathParser testDataSourcePath = new FilePathParser(environmentVariables) {
            @Override
            protected String getFileSeparator() {
                return "/";
            }
        };
        assertThat(testDataSourcePath.getInstanciatedPath("$USERDIR/simple-semicolon-data.csv"), is("/home/john/work/simple-semicolon-data.csv"));

    }

    @Test
    public void should_inject_current_directory_into_path_using_USERPROFILE_variable() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("user.home","/home/john");
        FilePathParser testDataSourcePath = new FilePathParser(environmentVariables) {
            @Override
            protected String getFileSeparator() {
                return "/";
            }
        };
        assertThat(testDataSourcePath.getInstanciatedPath("%USERPROFILE%/simple-semicolon-data.csv"), is("/home/john/simple-semicolon-data.csv"));

    }

    @Test
    public void should_inject_Windows_application_directory_into_path_using_APPDATA_variable() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        ((MockEnvironmentVariables)environmentVariables).setValue("APPDATA","/home/john/work");
        FilePathParser testDataSourcePath = new FilePathParser(environmentVariables) {
            @Override
            protected String getFileSeparator() {
                return "/";
            }
        };
        assertThat(testDataSourcePath.getInstanciatedPath("%APPDATA%/simple-semicolon-data.csv"), is("/home/john/work/simple-semicolon-data.csv"));

    }

}


