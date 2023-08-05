package net.thucydides.model.reports.json.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.flags.Flag;
import net.thucydides.model.reports.json.AScenarioHasNoNameException;
import net.thucydides.model.reports.json.JSONConverter;
import net.thucydides.model.util.EnvironmentVariables;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class GsonJSONConverter implements JSONConverter {

    private final EnvironmentVariables environmentVariables;

    private final Gson gson;

    protected Gson getGson() {
        return gson;
    }

    private final String encoding;

    
    public GsonJSONConverter(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        encoding = ThucydidesSystemProperty.SERENITY_REPORT_ENCODING.from(environmentVariables, StandardCharsets.UTF_8.name());
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY)
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
        if (testOutcome == null || isEmpty(testOutcome.getId())) {
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
