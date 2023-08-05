package net.serenitybdd.plugins.crossbrowsertesting;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;

public class CrossBrowserTestingTestSession {

    private final String user;
    private final String key;
    private final String sessionId;
    public final String API = "https://crossbrowsertesting.com/api/v3/selenium/";

    public CrossBrowserTestingTestSession(String user, String key, String sessionId) {
        this.user = user;
        this.key = key;
        this.sessionId = sessionId;
    }

    public void updateTestResultsFor(TestOutcome testOutcome) {
        String score = cbtCompatibleResultOf(testOutcome);

        Unirest.put(API + this.sessionId)
                .basicAuth(user, key)
                .field("action", "set_score")
                .field("score", score)
                .asJson();
    }

    public String takeSnapshot() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest
                .post(API + this.sessionId + "/snapshots")
                .basicAuth(user, key)
                .asJson();

        return (String) response.getBody().getObject().get("hash");
    }

    public void takeSnapshot(String description) throws UnirestException {
        String hash = takeSnapshot();

        Unirest.put(API + "{seleniumTestId}/snapshots/{snapshotHash}")
                .basicAuth(user, key)
                .routeParam("seleniumTestId", this.sessionId)
                .routeParam("snapshotHash", hash)
                .field("description", description)
                .asJson();
    }

    public String getPublicUrl() {
        HttpResponse<JsonNode> response = Unirest
                .get(API + this.sessionId)
                .basicAuth(user, key)
                .asJson();

        return (String) response.getBody().getObject().get("show_result_public_url");
    }

    private String cbtCompatibleResultOf(TestOutcome outcome) {
        switch (latestResultOf(outcome)) {
            case SUCCESS:
                return "pass";
            case FAILURE:
            case ERROR:
            case COMPROMISED:
                return "fail";
            default:
                return "unset";
        }
    }

    private TestResult latestResultOf(TestOutcome outcome) {
        if (outcome.isDataDriven()) {
            return outcome.getLatestTopLevelTestStep().get().getResult();
        } else {
            return outcome.getResult();
        }
    }
}
