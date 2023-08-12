package net.serenitybdd.model.history;

import net.thucydides.model.domain.flags.Flag;

public enum NewFailure implements Flag {

    FLAG;

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
