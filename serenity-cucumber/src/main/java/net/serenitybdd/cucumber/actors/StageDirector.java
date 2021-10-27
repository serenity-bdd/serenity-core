package net.serenitybdd.cucumber.actors;

import io.cucumber.java.After;
import net.serenitybdd.screenplay.actors.OnStage;

public class StageDirector {
    @After
    public void endTheAct() {
        OnStage.drawTheCurtain();
    }
}
