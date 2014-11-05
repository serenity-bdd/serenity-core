package net.thucydides.junit.runners;

import net.thucydides.samples.SampleParallelDataDrivenScenario;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenSchedulingParallelTestJobs {

    @Mock
    Runnable testJob;

    @Mock
    Runnable testJob2;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void scheduling_a_parallel_test_run_should_add_it_to_the_scheduler_task_queue() {
        ParameterizedRunnerScheduler scheduler
                = new ParameterizedRunnerScheduler(SampleParallelDataDrivenScenario.class, 4);


        scheduler.schedule(testJob);

        assertThat(scheduler.getTaskQueue().size(), is(1));
    }

    @Test
    public void scheduled_parallel_tests_should_all_be_processed_when_the_run_is_finished() {
        ParameterizedRunnerScheduler scheduler
                = new ParameterizedRunnerScheduler(SampleParallelDataDrivenScenario.class, 4);


        scheduler.schedule(testJob);
        scheduler.schedule(testJob2);

        assertThat(scheduler.getTaskQueue().size(), is(2));
        scheduler.finished();
        assertThat(scheduler.getTaskQueue().size(), is(0));
    }

}
