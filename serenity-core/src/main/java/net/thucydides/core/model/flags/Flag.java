package net.thucydides.core.model.flags;

public interface Flag {
    String getType();
    String getMessage();

    /**
     * The Font-Awesome symbol used to represent this flag
     */
    String getSymbol();
}
