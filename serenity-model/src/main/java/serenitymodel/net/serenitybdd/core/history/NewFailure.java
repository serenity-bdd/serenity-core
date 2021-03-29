package serenitymodel.net.serenitybdd.core.history;

import serenitymodel.net.thucydides.core.model.flags.Flag;

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
