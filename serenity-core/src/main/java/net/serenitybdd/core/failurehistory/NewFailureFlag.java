package net.serenitybdd.core.failurehistory;

import net.thucydides.core.model.flags.Flag;

public class NewFailureFlag implements Flag {
    @Override
    public String getType() {
        return "new-failure";
    }

    @Override
    public String getMessage() {
        return "New failure";
    }

    @Override
    public String getSymbol() {
        return "flag";
    }
}
