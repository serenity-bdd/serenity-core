package junittestcases.samples.mock;

import net.thucydides.model.tags.Taggable;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * Created by john on 8/12/14.
 */
public class SerenityRunner extends Runner implements Taggable {

    @Override
    public Description getDescription() {
        return null;
    }

    @Override
    public void run(RunNotifier notifier) {

    }
}
