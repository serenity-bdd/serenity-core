package net.serenitybdd.junit.runners;


import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static net.thucydides.core.ThucydidesSystemProperty.RECORD_FAILURES;
import static net.thucydides.core.ThucydidesSystemProperty.REPLAY_FAILURES;

public class FailureRerunnerXml implements FailureRerunner {


    private final Logger logger = LoggerFactory.getLogger(FailureRerunnerXml.class);
    private final EnvironmentVariables environmentVariables;
    private final static String DEFAULT_RERUN_FOLDER_NAME = "rerun";
    private JAXBContext jaxbContext;
    private String rerunFolderName;

    public FailureRerunnerXml(Configuration configuration) {
        this.environmentVariables = configuration.getEnvironmentVariables();
        this.rerunFolderName = ThucydidesSystemProperty.RERUN_FAILURES_DIRECTORY.from(environmentVariables, DEFAULT_RERUN_FOLDER_NAME);
        try {
            jaxbContext = JAXBContext.newInstance(RerunnableClass.class);
        } catch (JAXBException e) {
            logger.error("cannot initialize jaxbContext",e);
        }
    }

    public void recordFailedTests(Map<String, List<String>> failedTests) {
        if(!RECORD_FAILURES.booleanFrom(environmentVariables, false))
        {
            return;
        }
        if(failedTests.size() == 0) {
            logger.info(" no failed tests to record" );
            return;
        }
        Path rerunFolder = Paths.get(rerunFolderName);
        if(!Files.exists(rerunFolder)) {
            try {
                Files.createDirectory(rerunFolder);
            }
            catch(FileAlreadyExistsException ex) {
                logger.error(" directory of rerun files already exists " );
            }
            catch (IOException e) {
                logger.error(" cannot create directory of rerun files " );
                return;
            }
        }
        try {
            RerunnableClass rerunnableClass = null;
            for(Map.Entry<String,List<String>> entry : failedTests.entrySet()) {
                String className = entry.getKey().replace("$", ".");
                Path rerunFile = Paths.get(rerunFolderName, className + "_rerun.xml");
                logger.info("recording failing tests in file " + rerunFile);
                if (Files.exists(rerunFile)) {
                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                    rerunnableClass = (RerunnableClass) jaxbUnmarshaller.unmarshal(rerunFile.toFile());
                }
                if (rerunnableClass == null) {
                    rerunnableClass = new RerunnableClass();
                    rerunnableClass.setClassName(className);
                }
                for (String failedTestMethodName : entry.getValue()) {
                    logger.info("Adding failedTestMethodName " + failedTestMethodName);
                    rerunnableClass.getMethodNames().add(failedTestMethodName);
                }
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.marshal(rerunnableClass, rerunFile.toFile());
            }
        } catch(Throwable th) {
            logger.error("Error recording failing tests " + th.getMessage(), th);
        }
    }

    public boolean hasToRunTest(String className,String methodName) {

        if(!REPLAY_FAILURES.booleanFrom(environmentVariables, false))
        {
            return true;
        }
        logger.info("Check if must rerun method " + className + " " + methodName);
        try {
            Path rerunFile = Paths.get(rerunFolderName,className+ "_rerun.xml");
            if(Files.exists(rerunFile)) {
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                RerunnableClass rerunnableClass = (RerunnableClass) jaxbUnmarshaller.unmarshal(rerunFile.toFile());
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
