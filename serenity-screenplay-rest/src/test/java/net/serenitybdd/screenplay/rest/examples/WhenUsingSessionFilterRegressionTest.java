package net.serenitybdd.screenplay.rest.examples;

import io.restassured.filter.session.SessionFilter;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Get;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;

@ExtendWith(SerenityJUnit5Extension.class)
public class WhenUsingSessionFilterRegressionTest {

  @RegisterExtension
  static WireMockExtension sessionsWireMock = WireMockExtension.newInstance().options(WireMockConfiguration.options()
          .dynamicPort()
          .usingFilesUnderClasspath("wiremock/sessions"))
      .build();

  Actor actor;

  @BeforeEach
  void prepare() {
    actor = Actor.named("actor").whoCan(CallAnApi.at(sessionsWireMock.getRuntimeInfo().getHttpBaseUrl()));
  }

  @Test
  public void didThrowNPE() {
    actor.attemptsTo(Get.resource("/session-check").with(request -> request.filter(new SessionFilter())));
    actor.should(seeThatResponse("bad request", response -> response.statusCode(200)));
  }

  @Test
  public void didSucceed() {
    actor.attemptsTo(Get.resource("/session-check").with(request -> {
      request.filter(new SessionFilter());
      return request;
    }));
    actor.should(seeThatResponse("bad request", response -> response.statusCode(200)));
  }

  @Test
  void canStoreSessionUsingSessionFilter() {
    final SessionFilter sessionFilter = new SessionFilter();

    actor.attemptsTo(Get.resource("/session-check").with(request -> request.filter(sessionFilter)));
    actor.should(
        seeThatResponse("successful response", response -> response.statusCode(200)),
        seeThatResponse(response -> response.body("message", Matchers.equalTo("New session started"))),
        seeThatResponse(response -> response.cookie("JSESSIONID", Matchers.notNullValue()))
    );

    // The 2nd attempt leads to a different response body
    actor.attemptsTo(Get.resource("/session-check").with(request -> request.filter(sessionFilter)));
    actor.should(
        seeThatResponse("successful response", response -> response.statusCode(200)),
        seeThatResponse(response -> response.body("message", Matchers.equalTo("Hi! Welcome back!")))
    );
  }

  @Test
  void sessionIsNotStoredWithoutSessionFilter() {
    actor.attemptsTo(Get.resource("/session-check"));
    actor.should(
        seeThatResponse("successful response", response -> response.statusCode(200)),
        seeThatResponse(response -> response.body("message", Matchers.equalTo("New session started")))
    );

    // The 2nd attempt leads to the same response
    actor.attemptsTo(Get.resource("/session-check"));
    actor.should(
        seeThatResponse("successful response", response -> response.statusCode(200)),
        seeThatResponse(response -> response.body("message", Matchers.equalTo("New session started")))
    );
  }
}
