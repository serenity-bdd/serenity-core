package net.serenitybdd.junit.runners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.thucydides.core.ThucydidesSystemProperty.RECORD_FAILURES;
import static net.thucydides.core.ThucydidesSystemProperty.REPLAY_FAILURES;

public class FailureRerunnerJson implements FailureRerunner {

    private final Logger logger = LoggerFactory.getLogger(FailureRerunnerJson.class);
    private final EnvironmentVariables environmentVariables;
    private final static String DEFAULT_RERUN_FOLDER_NAME = "rerun";
    private ObjectMapper objectMapper;
    private String rerunFolderName;

    public FailureRerunnerJson(Configuration configuration) {
        this.environmentVariables = configuration.getEnvironmentVariables();
        this.rerunFolderName = ThucydidesSystemProperty.RERUN_FAILURES_DIRECTORY.from(environmentVariables, DEFAULT_RERUN_FOLDER_NAME);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void recordFailedTests(Map<String, List<String>> failedTests) {
        if(!RECORD_FAILURES.booleanFrom(environmentVariables, false)) {
            return;
        }
        if(failedTests.size() == 0) {
            logger.info("no failed tests to record");
            return;
        }
        Path rerunFolder = Paths.get(rerunFolderName);
        if(!Files.exists(rerunFolder)) {
            try {
                Files.createDirectory(rerunFolder);
            } catch (FileAlreadyExistsException ex) {
                logger.error("directory of rerun files already exists");
            } catch (IOException e) {
                logger.error("cannot create directory of rerun files");
                return;
            }
        }
        try {
            RerunnableClass rerunnableClass = null;
            for(Map.Entry<String,List<String>> entry : failedTests.entrySet()) {
                String className = entry.getKey().replace("$", ".");
                Path rerunFile = Paths.get(rerunFolderName, className + "_rerun.json");
                logger.info("recording failing tests in file " + rerunFile);
                if (Files.exists(rerunFile)) {
                    rerunnableClass = objectMapper.readValue(rerunFile.toFile(), RerunnableClass.class);
                }
                if (rerunnableClass == null) {
                    rerunnableClass = new RerunnableClass();
                    rerunnableClass.setClassName(className);
                }
                for (String failedTestMethodName : entry.getValue()) {
                    logger.info("Adding failedTestMethodName " + failedTestMethodName);
                    rerunnableClass.getMethodNames().add(failedTestMethodName);
                }
                objectMapper.writeValue(rerunFile.toFile(), rerunnableClass);
            }
        } catch(Throwable th) {
            logger.error("Error recording failing tests " + th.getMessage(), th);
        }
    }

    public boolean hasToRunTest(String className,String methodName) {
        if(!REPLAY_FAILURES.booleanFrom(environmentVariables, false)) {
            return true;
        }
        logger.info("Check if must rerun method " + className + " " + methodName);
        try {
            Path rerunFile = Paths.get(rerunFolderName, className + "_rerun.json");
            if(Files.exists(rerunFile)) {
                RerunnableClass rerunnableClass = objectMapper.readValue(rerunFile.toFile(), RerunnableClass.class);
                if(rerunnableClass.getClassName().equals(className) && rerunnableClass.getMethodNames().contains(methodName)) {
                    logger.info("Found rerunnable method " + methodName);
                    return true;
                }
            }
        } catch(Throwable th) {
            logger.error("Error when checking if method must be rerun: " + th.getMessage(), th);
        }
        return false;
    }
}
