package net.thucydides.core.reports.json.gson;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.json.AScenarioHasNoNameException;
import net.thucydides.core.reports.json.JSONConverter;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class GsonJSONConverter implements JSONConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GsonJSONConverter.class);

    private final EnvironmentVariables environmentVariables;

    Gson gson;

    protected Gson getGson() {
        return gson;
    }

    private final String encoding;

    @Inject
    public GsonJSONConverter(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        encoding = ThucydidesSystemProperty.THUCYDIDES_REPORT_ENCODING.from(environmentVariables, StandardCharsets.UTF_8.name());
        GsonBuilder gsonBuilder = new GsonBuilder()
                                            .registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY)
                                            .registerTypeHierarchyAdapter(Collection.class, new CollectionAdapter())
                                            .registerTypeAdapter(File.class, new FileSerializer())
                                            .registerTypeAdapter(File.class, new FileDeserializer())
                                            .registerTypeAdapter(Class.class, new ClassTypeAdapter());
        this.gson = (usePrettyPrinting()) ? gsonBuilder.setPrettyPrinting().create() : gsonBuilder.create();
    }

    @Override
    public Optional<TestOutcome> fromJson(InputStream inputStream) throws IOException {
        return fromJson(new InputStreamReader(inputStream, encoding));
    }

    @Override
    public Optional<TestOutcome> fromJson(Reader jsonReader) {
        TestOutcome testOutcome = gson.fromJson(jsonReader, TestOutcome.class);
        return isValid(testOutcome) ? Optional.of(testOutcome) : Optional.<TestOutcome>absent();
    }

    private boolean isValid(TestOutcome testOutcome) {
        boolean isValidJsonForm = isNotEmpty(testOutcome.getId());
        if (isValidJsonForm) {
            checkForRequiredFieldsIn(testOutcome);
        }
        return isValidJsonForm;
    }

    private void checkForRequiredFieldsIn(TestOutcome testOutcome) {
        if (isEmpty(testOutcome.getName())) {
            throw new AScenarioHasNoNameException(testOutcome.getId());
        }
    }


    @Override
    public void toJson(TestOutcome testOutcome, OutputStream outputStream) throws IOException {
        testOutcome.calculateDynamicFieldValues();
        try(Writer out = new OutputStreamWriter(outputStream, encoding)) {
            gson.toJson(testOutcome, out);
        }
    }

    private boolean usePrettyPrinting() {
        return environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.JSON_PRETTY_PRINTING, false);
    }
}
