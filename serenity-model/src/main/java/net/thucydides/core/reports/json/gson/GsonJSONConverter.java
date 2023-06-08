package net.thucydides.core.reports.json.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.flags.Flag;
import net.thucydides.core.reports.json.AScenarioHasNoNameException;
import net.thucydides.core.reports.json.JSONConverter;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class GsonJSONConverter implements JSONConverter {

    private final EnvironmentVariables environmentVariables;

    private final Gson gson;

    protected Gson getGson() {
        return gson;
    }

    private final String encoding;

    @Inject
    public GsonJSONConverter(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        encoding = ThucydidesSystemProperty.SERENITY_REPORT_ENCODING.from(environmentVariables, StandardCharsets.UTF_8.name());
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY)
//                .registerTypeAdapterFactory(GuavaOptionalTypeAdapter.FACTORY)
                .registerTypeHierarchyAdapter(Collection.class, new CollectionAdapter())
                .registerTypeAdapter(Flag.class, new InterfaceAdapter<Flag>())
                .registerTypeAdapter(StackTraceElement.class, new StackTraceElementSerializer())
                .registerTypeAdapter(StackTraceElement.class, new StackTraceElementDeserializer())
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
                .registerTypeAdapter(File.class, new FileSerializer())
                .registerTypeAdapter(File.class, new FileDeserializer())
                .registerTypeAdapter(Class.class, new ClassTypeAdapter());
        this.gson = (usePrettyPrinting()) ? gsonBuilder.setPrettyPrinting().create() : gsonBuilder.create();
    }

    @Override
    public java.util.Optional<TestOutcome> fromJson(InputStream inputStream) throws IOException {
        return fromJson(new InputStreamReader(inputStream, encoding));
    }

    @Override
    public java.util.Optional<TestOutcome> fromJson(Reader jsonReader) {
        TestOutcome testOutcome = gson.fromJson(jsonReader, TestOutcome.class);
        return isValid(testOutcome) ? java.util.Optional.of(testOutcome) : java.util.Optional.empty();
    }

    private boolean isValid(TestOutcome testOutcome) {
        if (isEmpty(testOutcome.getId())) {
            return false;
        }
        checkForRequiredFieldsIn(testOutcome);
        return true;
    }

    private void checkForRequiredFieldsIn(TestOutcome testOutcome) {
        if (isEmpty(testOutcome.getName())) {
            throw new AScenarioHasNoNameException(testOutcome.getId());
        }
    }


    @Override
    public void toJson(TestOutcome testOutcome, OutputStream outputStream) throws IOException {
        testOutcome.calculateDynamicFieldValues();
        try (Writer out = new OutputStreamWriter(outputStream, encoding)) {
            gson.toJson(testOutcome, out);
        }
    }

    private boolean usePrettyPrinting() {
        return Boolean.parseBoolean(ThucydidesSystemProperty.JSON_PRETTY_PRINTING.from(environmentVariables, "false"));
    }
}
