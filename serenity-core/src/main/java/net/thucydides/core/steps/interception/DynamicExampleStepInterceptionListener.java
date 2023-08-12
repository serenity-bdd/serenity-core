package net.thucydides.core.steps.interception;

import com.google.common.base.Preconditions;
import net.serenitybdd.annotations.TestAnnotations;
import net.thucydides.core.steps.ExampleTables;
import net.thucydides.core.steps.StepEventBus;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicExampleStepInterceptionListener implements StepInterceptionListener {
    @Override
    public void start(Object obj, Method method, Object[] args, Method zuperMethod) {
        if (ExampleTables.isUsingAnExampleTable() && TestAnnotations.isAnExampleStep(method)) {

            List<String> headers = ExampleTables.getCurrentExampleTable().getHeaders();

            checkColumnCountFor(method, headers, args);

            Map<String, String> row = new HashMap();

            int argumentIndex = 0;
            for(String header : headers) {
                row.put(header, args[argumentIndex++].toString());
            }

            StepEventBus.getParallelEventBus().exampleStarted(row);
        }
    }

    private void checkColumnCountFor(Method method, List<String> headers, Object[] args) {
        Preconditions.checkArgument(args.length >= headers.size(), "Missing column values for " + method
                                                                            + " (expecting values for: " + headers);
    }

    @Override
    public void end(Object obj, Method method, Object[] args, Method zuperMethod) {
        if (ExampleTables.isUsingAnExampleTable() && TestAnnotations.isAnExampleStep(method)) {
            StepEventBus.getParallelEventBus().exampleFinished();
        }
    }
}
