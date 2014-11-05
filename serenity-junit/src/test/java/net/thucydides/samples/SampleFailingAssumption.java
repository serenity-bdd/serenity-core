package net.thucydides.samples;

import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ThucydidesRunner.class)
public class SampleFailingAssumption {

    @Test
    public void demonstrate_that_pending_test_is_marked_as_passed() {
        Assume.assumeTrue(false);
    }
}
