package net.thucydides.junit.runners;

import com.google.common.collect.ImmutableList;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.ArrayList;
import java.util.List;

public class TestFailureListener extends RunListener {
    
    private List<Failure> failures = new ArrayList<Failure>();
    
    @Override
    public void testFailure(Failure failure) throws Exception {
        failures.add(failure);
        super.testFailure(failure);
    }
    
    public List<Failure> getFailures() {
        return ImmutableList.copyOf(failures);
    }
}
