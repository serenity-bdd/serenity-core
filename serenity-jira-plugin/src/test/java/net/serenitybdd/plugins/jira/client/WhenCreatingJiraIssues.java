package net.serenitybdd.plugins.jira.client;

import com.sun.net.httpserver.HttpServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.serenitybdd.plugins.jira.domain.IssueSummary;
import net.serenitybdd.plugins.jira.model.JQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class WhenCreatingJiraIssues {
    private HttpServer server;
    private JerseyJiraClient client;
    private volatile String requestBody;
    private volatile String requestPath;

    @Before
    public void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.start();
        client = new JerseyJiraClient("http://127.0.0.1:" + server.getAddress().getPort(),
                "user", "token", "DEMO");
    }

    @After
    public void stopServer() {
        server.stop(0);
    }

    @Test
    public void reportsJiraValidationErrorsInsteadOfParsingThemAsAnIssue() {
        String error = "{\"errorMessages\":[],\"errors\":{\"issuetype\":\"Invalid issue type\"}}";
        respondWith(400, error);

        try {
            client.createIssue(newIssue());
            fail("Expected issue creation to fail");
        } catch (JQLException exception) {
            assertTrue(exception.getMessage().contains("400"));
            assertTrue(exception.getMessage().contains("/rest/api/3/issue"));
            assertTrue(exception.getMessage().contains(error));
        }
    }

    @Test
    public void readsASuccessfullyCreatedIssue() {
        respondWith(201, "{\"id\":\"123\",\"key\":\"DEMO-123\",\"self\":\"http://localhost/issue/123\"}");

        IssueSummary created = client.createIssue(newIssue());

        assertEquals("DEMO-123", created.getKey());
        assertEquals("http://localhost/issue/123", created.getSelf().toString());
        assertEquals("/rest/api/3/issue", requestPath);
    }

    @Test
    public void sendsTheDescriptionAsAnAtlassianDocument() {
        respondWith(201, "{\"id\":\"123\",\"key\":\"DEMO-123\",\"self\":\"http://localhost/issue/123\"}");
        IssueSummary issue = newIssue();
        issue.setDescription("First line\r\n\r\nA \"quoted\" line with \\ and ü");

        client.createIssue(issue);

        JsonObject expected = new JsonParser().parse("{\"type\":\"doc\",\"version\":1,\"content\":["
                + "{\"type\":\"paragraph\",\"content\":[{\"type\":\"text\",\"text\":\"First line\"}]},"
                + "{\"type\":\"paragraph\",\"content\":[]},"
                + "{\"type\":\"paragraph\",\"content\":[{\"type\":\"text\",\"text\":\"A \\\"quoted\\\" line with \\\\ and ü\"}]}]}").getAsJsonObject();
        assertEquals(expected, requestFields().getAsJsonObject("description"));
        assertEquals("DEMO", requestFields().getAsJsonObject("project").get("key").getAsString());
        assertEquals("1", requestFields().getAsJsonObject("issuetype").get("id").getAsString());
        assertEquals("A test issue", requestFields().get("summary").getAsString());
    }

    @Test
    public void omitsAnUnspecifiedDescription() {
        respondWith(201, "{\"id\":\"123\",\"key\":\"DEMO-123\",\"self\":\"http://localhost/issue/123\"}");

        client.createIssue(newIssue());

        assertFalse(requestFields().has("description"));
    }

    @Test
    public void sendsAnEmptyDescriptionWithoutAnEmptyTextNode() {
        respondWith(201, "{\"id\":\"123\",\"key\":\"DEMO-123\",\"self\":\"http://localhost/issue/123\"}");
        IssueSummary issue = newIssue();
        issue.setDescription("");

        client.createIssue(issue);

        assertEquals(new JsonParser().parse("{\"type\":\"doc\",\"version\":1,\"content\":["
                        + "{\"type\":\"paragraph\",\"content\":[]}]}"),
                requestFields().get("description"));
    }

    private JsonObject requestFields() {
        return new JsonParser().parse(requestBody).getAsJsonObject().getAsJsonObject("fields");
    }

    private IssueSummary newIssue() {
        IssueSummary issue = new IssueSummary();
        issue.setProject("DEMO");
        issue.setType("1");
        issue.setSummary("A test issue");
        return issue;
    }

    private void respondWith(int status, String body) {
        server.createContext("/rest/api/3/issue", exchange -> {
            requestPath = exchange.getRequestURI().getPath();
            try (java.io.InputStream input = exchange.getRequestBody()) {
                requestBody = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            }
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            try (java.io.OutputStream output = exchange.getResponseBody()) {
                output.write(bytes);
            }
        });
    }
}
