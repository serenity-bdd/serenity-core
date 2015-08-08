package net.serenitybdd.screenplay.executionstrategies;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.annotations.Methods;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.ReadableMethodName;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by john on 7/08/2015.
 */
public class RunnableTaskStrategy implements TaskExecutionStrategy {

    private final Actor actor;
    private final StepRecorder stepRecorder = new StepRecorder();

    public RunnableTaskStrategy(Actor actor) {
        this.actor = actor;
    }

    @Override
    public void perform(Task todo) {
        stepRecorder.recordStepStartFor(todo, actor);
        try {
            todo.performAs(actor);
            stepRecorder.recordSuccessfulStepFinishFor(todo);
        } catch (RuntimeException somethingWentWrong) {
            stepRecorder.dealWithFailure(somethingWentWrong);
        }
    }

    public class StepRecorder {
        private void recordSuccessfulStepFinishFor(Task todo) {
            StepEventBus.getEventBus().stepFinished();
        }

        private void recordStepStartFor(Task todo, Object... args) {
            String stepName = ReadableMethodName.forMethod(performAsMethodOn(todo))
                                                .withArguments(args)
                                                .asString();
            ExecutedStepDescription description = ExecutedStepDescription.of(todo.getClass(), stepName)
                    .withDisplayedFields(fieldValuesIn(todo));
            StepEventBus.getEventBus().stepStarted(description);
        }

        private Method performAsMethodOn(Task todo) {
            return Methods.of(todo.getClass()).called("performAs").first();
        }

        private Map<String, Object> fieldValuesIn(Object object) {
            return Fields.of(object).asMap();
        }

        public void dealWithFailure(RuntimeException somethingWentWrong) {
//            try {
                throw somethingWentWrong;
//            } catch (PendingStepException pendingStep) {
//                StepEventBus.getEventBus().stepPending(pendingStep.getMessage());
//            } catch (IgnoredStepException ignoredStep) {
//                StepEventBus.getEventBus().stepIgnored();
//            } catch (AssumptionViolatedException assumptionViolated) {
//                StepEventBus.getEventBus().assumptionViolated(assumptionViolated.getMessage());
//            } catch (AssertionError failedAssertion) {
//                error = failedAssertion;
//                logStepFailure(obj, method, args, failedAssertion);
//                result = appropriateReturnObject(obj, method);
//            } catch (AssumptionViolatedException assumptionFailed) {
//                result = appropriateReturnObject(obj, method);
//            } catch (Throwable testErrorException) {
//                error = SerenityWebDriverException.detachedCopyOf(testErrorException);
//                logStepFailure(obj, method, args, forError(error).convertToAssertion());
//                result = appropriateReturnObject(obj, method);
//            }

        }
    }

}
