package net.thucydides.core.reports.json.gson;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.json.JSONConverter;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class GsonJSONConverter implements JSONConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GsonJSONConverter.class);

    private final EnvironmentVariables environmentVariables;

    Gson gson;

    protected Gson getGson() {
        return gson;
    }

    @Inject
    public GsonJSONConverter(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        GsonBuilder gsonBuilder = new GsonBuilder()
                                            .registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY)
                                            .registerTypeHierarchyAdapter(Collection.class, new CollectionAdapter())
                                            .registerTypeAdapter(File.class, new FileSerializer())
                                            .registerTypeAdapter(File.class, new FileDeserializer())
                                            .registerTypeAdapter(Class.class, new ClassTypeAdapter());
        this.gson = (usePrettyPrinting()) ? gsonBuilder.setPrettyPrinting().create() : gsonBuilder.create();
    }

    @Override
    public TestOutcome fromJson(InputStream inputStream) throws IOException {
        return fromJson(new InputStreamReader(inputStream, Charsets.UTF_8));
    }

    @Override
    public TestOutcome fromJson(Reader jsonReader) {
        TestOutcome testOutcome = gson.fromJson(jsonReader, TestOutcome.class);
        LOGGER.debug("Read test outcome from JSON: " + testOutcome.toJson());
        return isValid(testOutcome) ? testOutcome : null;
    }

    private boolean isValid(TestOutcome testOutcome) {
        return isNotEmpty(testOutcome.getName());
    }

    @Override
    public void toJson(TestOutcome testOutcome, OutputStream outputStream) throws IOException {
        testOutcome.calculateDynamicFieldValues();
        try(Writer out = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            gson.toJson(testOutcome, out);
        }
    }

    private boolean usePrettyPrinting() {
        return environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.JSON_PRETTY_PRINTING, false);
    }
}
