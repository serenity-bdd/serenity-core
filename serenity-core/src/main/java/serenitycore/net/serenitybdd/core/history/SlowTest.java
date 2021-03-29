package serenitycore.net.serenitybdd.core.history;

import serenitymodel.net.thucydides.core.model.flags.Flag;

public enum SlowTest implements Flag {

    FLAG;

    @Override
    public String getType() {
        return "slow-test";
    }

    @Override
    public String getMessage() {
        return "Slow Test";
    }

    @Override
    public String getSymbol() {
        return "hourglass";
    }


}
