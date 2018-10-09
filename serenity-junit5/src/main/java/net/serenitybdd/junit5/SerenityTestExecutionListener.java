package net.serenitybdd.junit5;

import net.thucydides.core.model.DataTable;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.TestSourceType;
import org.junit.jupiter.api.Disabled;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SerenityTestExecutionListener implements TestExecutionListener {

    private final Logger logger = LoggerFactory.getLogger(SerenityTestExecutionListener.class);

    private TestPlan currentTestPlan;

    private SerenityTestExecutionSummary summary;

    //key-> "ClassName.MethodName"
    //entries-> DataTable associated with method
    private Map<String,DataTable> dataTables;

    private int parameterSetNumber = 0;

    @Override
    public  void testPlanExecutionStarted(TestPlan testPlan) {
        this.currentTestPlan = testPlan;
        this.summary = new SerenityTestExecutionSummary(testPlan);
        logger.debug("->TestPlanExecutionStarted " + testPlan);
        Set<TestIdentifier> roots = testPlan.getRoots();
        for(TestIdentifier root: roots) {
            //System.out.println("XXRoot " + root.getUniqueId() + root.getDisplayName() + root.getSource());
            Set<TestIdentifier> children = testPlan.getChildren(root.getUniqueId());
            for (TestIdentifier child : children) {
                //System.out.println("XXChild " + child.getUniqueId() + child.getDisplayName() + child.getSource() + child.getType());
                if(isClassSource(child))
                {
                    dataTables = JUnit5DataDrivenAnnotations.forClass(((ClassSource)child.getSource().get()).getJavaClass()).getParameterTables();
                }
            }
        }
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        logger.debug("->TestPlanExecutionFinished " + testPlan);
    }

    @Override
    public void dynamicTestRegistered(TestIdentifier testIdentifier) {
    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        processTestMethodAnnotationsFor(testIdentifier);
    }

    private void processTestMethodAnnotationsFor(TestIdentifier testIdentifier)  {
        Optional<TestSource> testSource = testIdentifier.getSource();
        if( testSource.isPresent() && (testSource.get() instanceof MethodSource)) {
            MethodSource methodTestSource = ((MethodSource)testIdentifier.getSource().get());
            String className =  methodTestSource.getClassName();
            String methodName = methodTestSource.getMethodName();
            String methodParameterTypes = methodTestSource.getMethodParameterTypes();
            try {
               if (isIgnored(Class.forName(className).getMethod(methodName))) {
                   startTestAtEventBus(testIdentifier);
                   StepEventBus.getEventBus().testIgnored();
                   StepEventBus.getEventBus().testFinished();
               }
            } catch(ClassNotFoundException | NoSuchMethodException exception) {
                logger.error("Exception when processing method annotations", exception);
            }
        }
    }

    private boolean isIgnored(Method child) {
        return child.getAnnotation(Disabled.class) != null;
    }


    private void startTestAtEventBus(TestIdentifier testIdentifier) {
        StepEventBus.getEventBus().setTestSource(TestSourceType.TEST_SOURCE_JUNIT.getValue());
        String displayName = removeEndBracketsFromDisplayName(testIdentifier.getDisplayName());
        if( isMethodSource(testIdentifier) ) {
            String className = ((MethodSource) testIdentifier.getSource().get()).getClassName();
            String methodName = ((MethodSource) testIdentifier.getSource().get()).getMethodName();
            try {
                StepEventBus.getEventBus().testStarted(
                        Optional.ofNullable(displayName).orElse("Initialisation"),
                        Class.forName(className));
            } catch(ClassNotFoundException  exception) {
                logger.error("Exception when starting test at event bus ", exception);
            }
        }
    }

    private String removeEndBracketsFromDisplayName(String displayName){
        if(displayName != null && displayName.endsWith("()")) {
            displayName = displayName.substring(0,displayName.length()-2);
        }
        return displayName;
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        logger.debug("-->Execution started " + testIdentifier.getDisplayName() +"--" +testIdentifier.getType() + "--" +testIdentifier.getSource());
        if(!testIdentifier.getSource().isPresent()) {
            logger.debug("No action done at executionStarted because testIdentifier is null" );
            return;
        }
        if(isMethodSource(testIdentifier)) {
            MethodSource methodSource = ((MethodSource)testIdentifier.getSource().get());
            String sourceMethod = methodSource.getClassName() + "." + methodSource.getMethodName();
            DataTable dataTable = dataTables.get(sourceMethod);
            if(dataTable != null) {
                if(testIdentifier.getType() == TestDescriptor.Type.CONTAINER){
                    StepEventBus.getEventBus().useExamplesFrom(dataTable);
                    parameterSetNumber = 0;
                } else if(testIdentifier.getType() == TestDescriptor.Type.TEST){
                    StepEventBus.getEventBus().exampleStarted(dataTable.row(parameterSetNumber).toStringMap());
                }
            }
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        logger.debug("-->Execution finished " + testIdentifier.getDisplayName() + "--" +testIdentifier.getType() + "--" + testIdentifier.getSource() +" with result " + testExecutionResult.getStatus());
        if(!testIdentifier.getSource().isPresent()) {
            logger.debug("No action done at executionFinished because testIdentifier is null" );
            return;
        }
        if(testIdentifier.getType() == TestDescriptor.Type.TEST){
            if(isMethodSource(testIdentifier)) {
                MethodSource methodSource = ((MethodSource)testIdentifier.getSource().get());
                String sourceMethod = methodSource.getClassName() + "." + methodSource.getMethodName();
                DataTable dataTable = dataTables.get(sourceMethod);
                if(dataTable != null) {
                    StepEventBus.getEventBus().exampleFinished();
                    parameterSetNumber++;
                }
            }
        }
        /*switch (testExecutionResult.getStatus()) {

            case SUCCESSFUL: {
                if (testIdentifier.isContainer()) {
                    this.summary.containersSucceeded.incrementAndGet();
                    System.out.println("CoNTAINER OK");
                }
                if (testIdentifier.isTest()) {
                    this.summary.testsSucceeded.incrementAndGet();
                    System.out.println("TEST OK");
                }
                break;
            }

            case ABORTED: {
                if (testIdentifier.isContainer()) {
                    this.summary.containersAborted.incrementAndGet();
                }
                if (testIdentifier.isTest()) {
                    this.summary.testsAborted.incrementAndGet();
                }
                break;
            }

            case FAILED: {
                if (testIdentifier.isContainer()) {
                    this.summary.containersFailed.incrementAndGet();
                }
                if (testIdentifier.isTest()) {
                    this.summary.testsFailed.incrementAndGet();
                }
                testExecutionResult.getThrowable().ifPresent(
                        throwable -> this.summary.addFailure(testIdentifier, throwable));
                break;
            }

            default:
                throw new PreconditionViolationException(
                        "Unsupported execution status:" + testExecutionResult.getStatus());
        }*/
    }

    @Override
    public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
        logger.debug("-->ReportingEntryPublished " + testIdentifier.getDisplayName() + "--" +testIdentifier.getType() + "--" + testIdentifier.getSource());
    }

    private boolean isClassSource(TestIdentifier testId){
        return testId.getSource().isPresent() && (testId.getSource().get() instanceof ClassSource);
    }

    private boolean isMethodSource(TestIdentifier testId){
        return testId.getSource().isPresent() && (testId.getSource().get() instanceof MethodSource);
    }
}
