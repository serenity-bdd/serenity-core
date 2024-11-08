package io.cucumber.junit.platform.engine;

import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;

import org.junit.jupiter.engine.config.DefaultJupiterConfiguration;
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.jupiter.engine.discovery.DiscoverySelectorResolver;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.config.PrefixedConfigurationParameters;
import org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.cucumber.core.options.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PARALLEL_CONFIG_PREFIX;
import static io.cucumber.junit.platform.engine.Constants.PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME;
import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_BATCH_COUNT;
import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_BATCH_NUMBER;
import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_FORK_COUNT;
import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_FORK_NUMBER;


public final class CucumberBatchTestEngine extends HierarchicalTestEngine<CucumberEngineExecutionContext> {

    static final Logger LOGGER = LoggerFactory.getLogger(CucumberBatchTestEngine.class);

    @Override
    public String getId() {
        return "cucumber-batch";
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        CucumberEngineDescriptor engineDescriptor = new CucumberEngineDescriptor(uniqueId);
        DefaultJupiterConfiguration jupiterConfiguration = new DefaultJupiterConfiguration(null);
        JupiterEngineDescriptor dd = new JupiterEngineDescriptor(uniqueId, jupiterConfiguration);
        new DiscoverySelectorResolver().resolveSelectors(discoveryRequest, dd);
        return engineDescriptor;
    }

    @Override
    protected HierarchicalTestExecutorService createExecutorService(ExecutionRequest request) {
        ConfigurationParameters config = request.getConfigurationParameters();
        if (config.getBoolean(PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME).orElse(false)) {
            return new ForkJoinPoolHierarchicalTestExecutorService(
                new PrefixedConfigurationParameters(config, PARALLEL_CONFIG_PREFIX));
        }

        if (!request.getRootTestDescriptor().getChildren().isEmpty()) {
            processRequestIfBatched(request);
        }

        return super.createExecutorService(request);
    }

    static void processRequestIfBatched(ExecutionRequest request) {
        //populate list
        String tagFilter = request.getConfigurationParameters().get(FILTER_TAGS_PROPERTY_NAME)
            .orElse(System.getProperty(FILTER_TAGS_PROPERTY_NAME));
        List<WeightedTest> scenarioList = request.getRootTestDescriptor().getChildren().stream()
            .map(TestDescriptor::getChildren)
            .flatMap(Set::stream)
            .map(WeightedTest::new)
            .collect(Collectors.toList());
        int total = scenarioList.size();
        List<WeightedTest> tagFilteredScenarioList = scenarioList.stream()
            .filter(scenario -> scenario.isTagMatchingFilter(tagFilter))
            .collect(Collectors.toList());
        LOGGER.info("Found {} scenarios in classpath, {} match(es) tag filter {}", total, tagFilteredScenarioList.size(), tagFilter);

        EnvironmentVariables envs = SystemEnvironmentVariables.currentEnvironmentVariables();
        int batchCount = envs.getPropertyAsInteger(SERENITY_BATCH_COUNT, 1);
        int batchNumber = envs.getPropertyAsInteger(SERENITY_BATCH_NUMBER, 1);
        int forkCount = envs.getPropertyAsInteger(SERENITY_FORK_COUNT, 1);
        int forkNumber = envs.getPropertyAsInteger(SERENITY_FORK_NUMBER, 1);

        LOGGER.info("Parameters: \n{}", request.getConfigurationParameters());
        LOGGER.info("Running partitioning for batch {} of {} and fork {} of {}", batchNumber,
                    batchCount, forkNumber, forkCount);

        List<WeightedTest> batch = getPartition(tagFilteredScenarioList, batchCount, batchNumber);
        List<WeightedTest> testToRun = getPartition(batch, forkCount, forkNumber);

        //prune and keep only test to run
        scenarioList.removeAll(testToRun);
        scenarioList.forEach(WeightedTest::removeFromHierarchy);

        LOGGER.info("Running {} of {} scenarios", testToRun.size(), total);
        LOGGER.info("Test to run: {}", testToRun);
        LOGGER.info("Root test descriptor has {} feature(s)",
                    request.getRootTestDescriptor().getChildren().size());
    }

    @Override
    protected CucumberEngineExecutionContext createExecutionContext(ExecutionRequest request) {
        return new CucumberEngineExecutionContext(request.getConfigurationParameters());
    }

    static List<WeightedTest> getPartition(List<WeightedTest> list, int partitions, int index) {
        if (partitions == 1 && index == 1) {
            return new ArrayList<>(list);
        }
        return getPartitionedTests(list, partitions).get(index - 1);
    }

    static List<List<WeightedTest>> getPartitionedTests(List<WeightedTest> list, int partitions) {
        List<List<WeightedTest>> result = Stream.generate(ArrayList<WeightedTest>::new)
            .limit(partitions)
            .collect(Collectors.toList());

        //sort all scenarios from large to small
        list.sort(Comparator.comparing(WeightedTest::getWeight).reversed());
        int[] weights = new int[partitions];

        for (WeightedTest test : list) {
            int minPartition = getMinPartition(weights);
            result.get(minPartition).add(test);
            weights[minPartition] += test.getWeight();
        }

        for (int i = 0; i < result.size(); i++) {
            LOGGER.info("{} of {}, weight = {}", i + 1, partitions,
                        result.get(i).stream().mapToInt(WeightedTest::getWeight).sum());
            LOGGER.info(print(result.get(i)));
        }
        return result;
    }

    private static String print(List<WeightedTest> list) {
        return list.stream().map(WeightedTest::toString).collect(Collectors.joining("\n"));
    }

    private static int getMinPartition(int[] weights) {
        return IntStream.range(0, weights.length)
            .boxed()
            .min(Comparator.comparingInt(i -> weights[i]))
            .orElse(-1);
    }
}
