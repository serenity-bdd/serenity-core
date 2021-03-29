package serenityscreenplay.net.serenitybdd.screenplay;

public interface RefersToActor {
    <T extends Ability> T asActor(Actor actor);
}
