package net.serenitybdd.screenplay;

import net.serenitybdd.core.SkipNested;

/**
 * A task or action that can be performed by an actor.
 *
 *
 * It is common to use builder methods to create instances of the Performable class, in order to make the tests read
 * more fluently. For example:
 *
 * [source,java]
 * --
 * purchase().anApple().thatCosts(0).dollars()
 * --
 *
 */
@FunctionalInterface
public interface Performable extends SkipNested {
    <T extends Actor> void performAs(T actor);
    default Performable then(Performable nextPerformable) {
        return CompositePerformable.from(this, nextPerformable);
    };
}
