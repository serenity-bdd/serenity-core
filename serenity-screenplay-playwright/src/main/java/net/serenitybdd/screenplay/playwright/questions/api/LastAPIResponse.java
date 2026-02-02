package net.serenitybdd.screenplay.playwright.questions.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.playwright.APIResponse;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.interactions.api.APIRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Query information about the most recent API response.
 *
 * <p>This question is used after an {@link APIRequest} interaction
 * to retrieve details about the API response.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Make an API request
 *     actor.attemptsTo(APIRequest.get("/api/users"));
 *
 *     // Query the response
 *     int status = actor.asksFor(LastAPIResponse.statusCode());
 *     String body = actor.asksFor(LastAPIResponse.body());
 *     boolean ok = actor.asksFor(LastAPIResponse.ok());
 *
 *     // Parse JSON response
 *     Map&lt;String, Object&gt; json = actor.asksFor(LastAPIResponse.jsonBody());
 *     List&lt;Map&lt;String, Object&gt;&gt; list = actor.asksFor(LastAPIResponse.jsonBodyAsList());
 *
 *     // Get specific header
 *     String contentType = actor.asksFor(LastAPIResponse.header("Content-Type"));
 *     Map&lt;String, String&gt; allHeaders = actor.asksFor(LastAPIResponse.headers());
 *
 *     // Get URL and status text
 *     String url = actor.asksFor(LastAPIResponse.url());
 *     String statusText = actor.asksFor(LastAPIResponse.statusText());
 * </pre>
 *
 * @see APIRequest
 */
public class LastAPIResponse {

    private static final Gson GSON = new Gson();

    private LastAPIResponse() {
        // Factory class - prevent instantiation
    }

    /**
     * Get the HTTP status code of the response.
     */
    public static Question<Integer> statusCode() {
        return new StatusCodeQuestion();
    }

    /**
     * Check if the response was successful (status 200-299).
     */
    public static Question<Boolean> ok() {
        return new OkQuestion();
    }

    /**
     * Get the HTTP status text of the response.
     */
    public static Question<String> statusText() {
        return new StatusTextQuestion();
    }

    /**
     * Get the response body as a string.
     */
    public static Question<String> body() {
        return new BodyQuestion();
    }

    /**
     * Get the response body as bytes.
     */
    public static Question<byte[]> bodyAsBytes() {
        return new BodyBytesQuestion();
    }

    /**
     * Get the response body parsed as a JSON object (Map).
     */
    public static Question<Map<String, Object>> jsonBody() {
        return new JsonBodyQuestion();
    }

    /**
     * Get the response body parsed as a JSON array (List of Maps).
     */
    public static Question<List<Map<String, Object>>> jsonBodyAsList() {
        return new JsonBodyAsListQuestion();
    }

    /**
     * Get a specific header value from the response.
     *
     * @param headerName The name of the header (case-insensitive)
     */
    public static Question<String> header(String headerName) {
        return new HeaderQuestion(headerName);
    }

    /**
     * Get all headers from the response.
     */
    public static Question<Map<String, String>> headers() {
        return new AllHeadersQuestion();
    }

    /**
     * Get the final URL after any redirects.
     */
    public static Question<String> url() {
        return new UrlQuestion();
    }

    /**
     * Get the raw APIResponse object for advanced operations.
     */
    public static Question<APIResponse> response() {
        return new ResponseObjectQuestion();
    }

    // Private helper to get the stored response
    private static APIResponse getResponse(Actor actor) {
        APIResponse response = actor.recall(APIRequest.getLastApiResponseKey());
        if (response == null) {
            throw new IllegalStateException(
                "No API response found. Use APIRequest to make a request first."
            );
        }
        return response;
    }

    // Question implementations

    static class StatusCodeQuestion implements Question<Integer> {
        @Override
        public Integer answeredBy(Actor actor) {
            return getResponse(actor).status();
        }

        @Override
        public String toString() {
            return "HTTP status code of the last API response";
        }
    }

    static class OkQuestion implements Question<Boolean> {
        @Override
        public Boolean answeredBy(Actor actor) {
            return getResponse(actor).ok();
        }

        @Override
        public String toString() {
            return "whether the last API response was successful";
        }
    }

    static class StatusTextQuestion implements Question<String> {
        @Override
        public String answeredBy(Actor actor) {
            return getResponse(actor).statusText();
        }

        @Override
        public String toString() {
            return "HTTP status text of the last API response";
        }
    }

    static class BodyQuestion implements Question<String> {
        @Override
        public String answeredBy(Actor actor) {
            return getResponse(actor).text();
        }

        @Override
        public String toString() {
            return "body of the last API response";
        }
    }

    static class BodyBytesQuestion implements Question<byte[]> {
        @Override
        public byte[] answeredBy(Actor actor) {
            return getResponse(actor).body();
        }

        @Override
        public String toString() {
            return "body bytes of the last API response";
        }
    }

    static class JsonBodyQuestion implements Question<Map<String, Object>> {
        @Override
        public Map<String, Object> answeredBy(Actor actor) {
            String body = getResponse(actor).text();
            if (body == null || body.isEmpty()) {
                return Collections.emptyMap();
            }
            return GSON.fromJson(body, new TypeToken<Map<String, Object>>(){}.getType());
        }

        @Override
        public String toString() {
            return "JSON body of the last API response as Map";
        }
    }

    static class JsonBodyAsListQuestion implements Question<List<Map<String, Object>>> {
        @Override
        public List<Map<String, Object>> answeredBy(Actor actor) {
            String body = getResponse(actor).text();
            if (body == null || body.isEmpty()) {
                return Collections.emptyList();
            }
            return GSON.fromJson(body, new TypeToken<List<Map<String, Object>>>(){}.getType());
        }

        @Override
        public String toString() {
            return "JSON body of the last API response as List";
        }
    }

    static class HeaderQuestion implements Question<String> {
        private final String headerName;

        HeaderQuestion(String headerName) {
            this.headerName = headerName;
        }

        @Override
        public String answeredBy(Actor actor) {
            Map<String, String> headers = getResponse(actor).headers();
            // Headers are case-insensitive, so check with lowercase
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(headerName)) {
                    return entry.getValue();
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "'" + headerName + "' header of the last API response";
        }
    }

    static class AllHeadersQuestion implements Question<Map<String, String>> {
        @Override
        public Map<String, String> answeredBy(Actor actor) {
            return getResponse(actor).headers();
        }

        @Override
        public String toString() {
            return "all headers of the last API response";
        }
    }

    static class UrlQuestion implements Question<String> {
        @Override
        public String answeredBy(Actor actor) {
            return getResponse(actor).url();
        }

        @Override
        public String toString() {
            return "URL of the last API response";
        }
    }

    static class ResponseObjectQuestion implements Question<APIResponse> {
        @Override
        public APIResponse answeredBy(Actor actor) {
            return getResponse(actor);
        }

        @Override
        public String toString() {
            return "the last API response object";
        }
    }
}
