package net.serenitybdd.screenplay.rest.examples;

import io.restassured.filter.session.SessionFilter;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Get;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;

@RunWith(SerenityRunner.class)
public class WhenUsingSessionFilterRegressionTest {

  Actor actor = Actor.named("actor").whoCan(CallAnApi.at("https://reqres.in"));

  @Test
  public void didThrowNPE() {
    actor.attemptsTo(Get.resource("/").with(request -> request.filter(new SessionFilter())));
    actor.should(seeThatResponse("bad request", response -> response.statusCode(200)));

  }
  @Test
  public void didSucceed() {
    actor.attemptsTo(Get.resource("/").with(request -> {
      request.filter(new SessionFilter());
      return request;
    }));
    actor.should(seeThatResponse("bad request", response -> response.statusCode(200)));
  }
}