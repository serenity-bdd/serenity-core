package net.thucydides.model.domain.flags;

public interface Flag {
    /**
     * A unique identifier for this type of flag
     */
    String getType();

    /**
     * A description of the flag type
     */
    String getMessage();

    /**
     * The Font-Awesome symbol used to represent this flag
     */
    String getSymbol();
}
