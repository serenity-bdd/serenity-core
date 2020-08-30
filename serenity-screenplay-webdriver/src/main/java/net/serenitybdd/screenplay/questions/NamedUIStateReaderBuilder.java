package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.targets.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamedUIStateReaderBuilder<T> extends UIStateReaderBuilder<T> {
    private String attributeName;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public NamedUIStateReaderBuilder(Target target, Class<T> type) {
        super(target, type);
    }

}
