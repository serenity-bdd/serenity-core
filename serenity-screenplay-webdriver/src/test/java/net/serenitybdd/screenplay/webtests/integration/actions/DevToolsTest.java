package net.serenitybdd.screenplay.webtests.integration.actions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.actions.DevToolsQuery;
import net.serenitybdd.screenplay.actions.WithDevTools;
import net.serenitybdd.screenplay.webtests.integration.ScreenplayInteractionTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.devtools.v100.performance.Performance;
import org.openqa.selenium.devtools.v100.performance.model.Metric;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class DevToolsTest extends ScreenplayInteractionTestBase {

    @Test
    public void useDevToolsToFetchBrowserMetrics() {

        final List<Metric> metricList = new ArrayList<>();

        dina.attemptsTo(
                WithDevTools.perform(
                        devTools -> {
                            devTools.createSession();
                            devTools.send(Performance.enable(Optional.empty()));
                            metricList.addAll(devTools.send(Performance.getMetrics()));
                        }
                )
        );
        Number firstMeaningfulPaint = metricList.stream()
                .filter(metric -> metric.getName().equals("FirstMeaningfulPaint"))
                .findFirst()
                .get().getValue();
        assertThat(firstMeaningfulPaint).isNotNull();
    }

    @Test
    public void useDevToolsToQueryBrowserMetrics() {

        List<Metric> metrics = dina.asksFor(
                DevToolsQuery.ask().about(devTools -> {
                    devTools.createSession();
                    devTools.send(Performance.enable(Optional.empty()));
                    return devTools.send(Performance.getMetrics());
                })
        );

        Number firstMeaningfulPaint = metrics.stream()
                .filter(metric -> metric.getName().equals("FirstMeaningfulPaint"))
                .findFirst()
                .get().getValue();
        assertThat(firstMeaningfulPaint).isNotNull();
    }
}
