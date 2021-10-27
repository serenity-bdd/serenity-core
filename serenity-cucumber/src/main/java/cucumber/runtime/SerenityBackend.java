package cucumber.runtime;

import io.cucumber.core.backend.Backend;
import io.cucumber.core.backend.Glue;
import io.cucumber.core.backend.Snippet;
import net.thucydides.core.steps.StepEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

public class SerenityBackend implements Backend {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerenityBackend.class);
    
    public SerenityBackend() {

    }

    /**
     * Invoked once before all features. This is where stepdefs and hooks should be loaded.
     */

    @Override
    public void loadGlue(Glue glue, List<URI> gluePaths){

    }

    /**
     * Invoked before a new scenario starts. Implementations should do any necessary
     * setup of new, isolated state here.
     */
    @Override
    public void buildWorld(){}



    
    /**
     * Invoked at the end of a scenario, after hooks
     */
    @Override
    public void disposeWorld() {
        if (!StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            LOGGER.warn("It looks like you are running a feature using @RunWith(Cucumber.class) instead of @RunWith(CucumberWithSerenity.class). Are you sure this is what you meant to do?");
        }
    }

    @Override
    public Snippet getSnippet() {
        return null;
    }


}
