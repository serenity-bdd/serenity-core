package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.thucydides.core.model.*;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;

public class TestOutcomeModule extends SimpleModule {

    public TestOutcomeModule() {
        super("TestOutcomes", new Version(0,0,1,"RELEASE","net.thucydides.core","thucydides-core-json"));
    }

    public TestOutcomeModule(String name, Version version) {
        super(name, version);
    }

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(TestOutcome.class, JSONTestOutcomeMixin.class);
        context.setMixInAnnotations(Story.class, JSONStoryMixin.class);
        context.setMixInAnnotations(TestStep.class, JSONTestStepMixin.class);
        context.setMixInAnnotations(ApplicationFeature.class, JSONFeatureMixin.class);
        context.setMixInAnnotations(TestTag.class, JSONTestTagMixin.class);
        context.setMixInAnnotations(ScreenshotAndHtmlSource.class, JSONScreenshotAndHtmlMixin.class);
        context.setMixInAnnotations(DataTable.class, JSONDataTableMixin.class);
        context.setMixInAnnotations(DataSetDescriptor.class, JSONDataSetDescriptorMixin.class);
        context.setMixInAnnotations(DataTableRow.class, JSONDataTableRowMixin.class);
        context.setMixInAnnotations(StackTraceElement.class, JSONStackTraceElementMixin.class);
        context.setMixInAnnotations(Throwable.class, JSONThrowableMixin.class);
        context.setMixInAnnotations(FailureCause.class, JSONFailureCauseMixin.class);


    }
}
