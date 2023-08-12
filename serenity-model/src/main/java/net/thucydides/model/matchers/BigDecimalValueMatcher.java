package net.thucydides.model.matchers;

import net.thucydides.model.reflection.FieldValue;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.Matchers.closeTo;

public class BigDecimalValueMatcher {
    private final Number value;
    private final Matcher<? extends Object> matcher;

    protected BigDecimalValueMatcher(Number value, Matcher<? extends BigDecimal> matcher) {
        this.value = value;
        Object expectedValue = expectedValue(matcher);
        if ((matcher instanceof IsEqual) || (matcher instanceof Is)) {
            this.matcher = closeTo(new BigDecimal(expectedValue.toString()), new BigDecimal("0"));
        } else {
            this.matcher = matcher;
        }
    }

    private Object expectedValue(Matcher matcher) {
        if (matcher.getClass() == Is.class) {
            Matcher innerMatcher = (Matcher) FieldValue.inObject(matcher).fromFieldNamed("matcher").get();
            Optional<Object> fieldValue = FieldValue.inObject(innerMatcher).fromFieldNamed("expectedValue");
            if (!fieldValue.isPresent()) {
                fieldValue = FieldValue.inObject(innerMatcher).fromFieldNamed("expected");
            }
            return fieldValue.orElse(null);
        } else {
            Optional<Object> fieldValue = FieldValue.inObject(matcher).fromFieldNamed("expectedValue");
            if (!fieldValue.isPresent()) {
                fieldValue = FieldValue.inObject(matcher).fromFieldNamed("expected");
            }
            return fieldValue.orElse(null);
        }
    }

    public boolean matches() {
        return matcher.matches(new BigDecimal(value.toString()));
    }
}
