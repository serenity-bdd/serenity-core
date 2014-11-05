package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.json.JSONConverter;
import net.thucydides.core.util.EnvironmentVariables;

import javax.validation.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;

public class JacksonJSONConverter implements JSONConverter {

    private final ObjectMapper mapper;
    private final ObjectReader reader;
    private final ObjectWriter writer;
    private final EnvironmentVariables environmentVariables;
    private final Validator validator;

    @Inject
    public JacksonJSONConverter(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new TestOutcomeModule());
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        reader = mapper.reader(TestOutcome.class);
        writer = mapper.writerWithType(TestOutcome.class);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    /**
     * For testing purposes.
     */
    protected ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public TestOutcome fromJson(InputStream inputStream) throws IOException {
        TestOutcome outcome = reader.readValue(inputStream);
        checkConstraints(outcome);
        return outcome;
    }

    private void checkConstraints(TestOutcome outcome) {
        Set<ConstraintViolation<TestOutcome>> violations = validator.validate(outcome);
        if (!violations.isEmpty()) {
            throw new ValidationException(Arrays.toString(violations.toArray()));
        }
    }

    @Override
    public void toJson(TestOutcome testOutcome, OutputStream outputStream) throws IOException {
        if (usePrettyPrinting()) {
            writer.withDefaultPrettyPrinter().writeValue(outputStream, testOutcome);
        } else {
            writer.writeValue(outputStream, testOutcome);
        }
    }

    private boolean usePrettyPrinting() {
        return environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.JSON_PRETTY_PRINTING, false);
    }
}
