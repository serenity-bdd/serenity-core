package net.thucydides.core.reports.json.gson;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.json.JSONConverter;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.*;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class GsonJSONConverter implements JSONConverter {

    private final EnvironmentVariables environmentVariables;

    Gson gson;

    protected Gson getGson() {
        return gson;
    }

    @Inject
    public GsonJSONConverter(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        GsonBuilder gsonBuilder = Converters.registerAll(new GsonBuilder())
                                            .registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY)
                                            .registerTypeHierarchyAdapter(Collection.class, new CollectionAdapter())
                                            .registerTypeAdapter(File.class, new FileSerializer())
                                            .registerTypeAdapter(File.class, new FileDeserializer())
                                            .registerTypeAdapter(Class.class, new ClassTypeAdapter());
        this.gson = (usePrettyPrinting()) ? gsonBuilder.setPrettyPrinting().create() : gsonBuilder.create();
    }

    @Override
    public TestOutcome fromJson(InputStream inputStream) throws IOException {
        TestOutcome testOutcome = gson.fromJson(new InputStreamReader(inputStream), TestOutcome.class);
        return isValid(testOutcome) ? testOutcome : null;
    }

    private boolean isValid(TestOutcome testOutcome) {
        return isNotEmpty(testOutcome.getName());
    }

    @Override
    public void toJson(TestOutcome testOutcome, OutputStream outputStream) throws IOException {
        testOutcome.calculateDynamicFieldValues();
        try(Writer out = new OutputStreamWriter(outputStream)) {
            gson.toJson(testOutcome, out);
        }
    }

    private boolean usePrettyPrinting() {
        return environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.JSON_PRETTY_PRINTING, false);
    }
}
