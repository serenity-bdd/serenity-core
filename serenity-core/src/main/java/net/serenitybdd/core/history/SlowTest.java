package net.serenitybdd.core.history;


import net.thucydides.model.domain.flags.Flag;

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
