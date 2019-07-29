package net.serenitybdd.screenplay.waits;

public interface WithTimeout {
    WithTimeUnits forNoLongerThan(long timeout);
    WithTimeUnits forNoLongerThan(int timeout);
}
