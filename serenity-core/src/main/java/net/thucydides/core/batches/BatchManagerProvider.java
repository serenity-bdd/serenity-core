package net.thucydides.core.batches;

import com.google.inject.Inject;
import com.google.inject.Provider;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;

public class BatchManagerProvider implements Provider<BatchManager>{
	
	private final BatchManager batchManager;
	
	@Inject
	public BatchManagerProvider(Configuration configuration){
		EnvironmentVariables environmentVariables = configuration.getEnvironmentVariables();
        String batchManagerProperty = ThucydidesSystemProperty.THUCYDIDES_BATCH_STRATEGY.from(environmentVariables,
                BatchStrategy.DIVIDE_EQUALLY.name());
        try {
        	batchManager = BatchStrategy.valueOf(batchManagerProperty).instance(environmentVariables);
        } catch (Exception e) {
            throw new UnsupportedBatchStrategyException(batchManagerProperty + " is not a supported batch strategy.", e);
        }
	}

	@Override
	public BatchManager get() {
		return batchManager;
	}

}
