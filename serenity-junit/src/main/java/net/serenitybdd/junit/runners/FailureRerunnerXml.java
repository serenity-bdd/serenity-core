package net.serenitybdd.junit.runners;


import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.thucydides.core.ThucydidesSystemProperty.RECORD_FAILURES;
import static net.thucydides.core.ThucydidesSystemProperty.REPLAY_FAILURES;

public class FailureRerunnerXml implements FailureRerunner {


    private final Logger logger = LoggerFactory.getLogger(FailureRerunnerXml.class);
    private final EnvironmentVariables environmentVariables;

    public FailureRerunnerXml(Configuration configuration){
        this.environmentVariables = configuration.getEnvironmentVariables();
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
        String defaultRerunFileName = "rerun.xml";
        String rerunFileName = ThucydidesSystemProperty.RERUN_FAILURES_FILE.from(environmentVariables, defaultRerunFileName);
        File rerunFile = new File(rerunFileName);
        try {
            logger.info("recording failing tests in file " + rerunFile);
            RerunnableClasses existingRerunnableClasses = null;
            Set<RerunnableClass> allRerunnableClasses = new HashSet<>();
            if(rerunFile.exists()) {
                JAXBContext jaxbContext = JAXBContext.newInstance(RerunnableClasses.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                existingRerunnableClasses = (RerunnableClasses) jaxbUnmarshaller.unmarshal(rerunFile);
                allRerunnableClasses = existingRerunnableClasses.getRerunnableClasses();
            }
            for(Map.Entry<String,List<String>> entry : failedTests.entrySet()) {
                RerunnableClass rerunnableClass =  null;
                if(existingRerunnableClasses != null) {
                    rerunnableClass = existingRerunnableClasses.getRerunnableClassWithName(entry.getKey());
                }
                if(rerunnableClass == null) {
                    rerunnableClass = new RerunnableClass();
                    rerunnableClass.setClassName(entry.getKey().replace("$","."));
                    allRerunnableClasses.add(rerunnableClass);
                }
                for(String failedTestMethodName : entry.getValue()) {
                    logger.info("Adding failedTestMethodName " + failedTestMethodName);
                    rerunnableClass.getMethodNames().add(failedTestMethodName);
                }
            }
            RerunnableClasses rerunnableClasses = new RerunnableClasses();
            rerunnableClasses.setRerunnableClasses(allRerunnableClasses);

            JAXBContext jaxbContext = JAXBContext.newInstance(RerunnableClasses.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(rerunnableClasses, rerunFile);
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
            String defaultRerunFileName = environmentVariables.getProperty(SystemPropertiesConfiguration.PROJECT_BUILD_DIRECTORY) + File.separator + "rerun.xml";
            String rerunFileName = ThucydidesSystemProperty.RERUN_FAILURES_FILE.from(environmentVariables, defaultRerunFileName);
            File rerunFile = new File(rerunFileName);
            if(rerunFile.exists()) {
                Unmarshaller jaxbUnmarshaller = JAXBContext.newInstance(RerunnableClasses.class).createUnmarshaller();
                RerunnableClasses rerunnableClasses = (RerunnableClasses) jaxbUnmarshaller.unmarshal(rerunFile);
                for (RerunnableClass rerunnableClass : rerunnableClasses.getRerunnableClasses()) {
                    if(rerunnableClass.getClassName().equals(className)) {
                        if (rerunnableClass.getMethodNames().contains(methodName)) {
                            logger.info("Found rerunnable method " + methodName);
                            return true;
                        }
                    }
                }
            }
        } catch(Throwable th) {
            logger.error("Error when checking if method must be rerun: " + th.getMessage(), th);
        }
        return false;
    }
}
