package net.serenitybdd.plugins.jira.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import net.serenitybdd.plugins.jira.domain.IssueComment;
import net.serenitybdd.plugins.jira.model.JQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.*;

public class WhenManagingJiraComments {
    private HttpServer server;
    private JerseyJiraClient client;
    private String baseUrl;
    private volatile String requestBody;
    private volatile String requestMethod;
    private int writeStatus = 201;
    private static final String DOCUMENT = "{\"type\":\"doc\",\"version\":1,\"content\":["
            + "{\"type\":\"paragraph\",\"content\":[{\"type\":\"text\",\"text\":\"A new comment\"}]}]}";

    @Before
    public void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        server.createContext("/rest/api/3/issue/DEMO-1/comment", exchange -> {
            String method = exchange.getRequestMethod();
            String response;
            int status;
            if ("GET".equals(method)) {
                status = 200;
                response = "{\"comments\":[" + commentJson(DOCUMENT) + "]}";
            } else {
                try (java.io.InputStream input = exchange.getRequestBody()) {
                    requestBody = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                }
                requestMethod = method;
                status = writeStatus;
                response = status == 400 ? "{\"errors\":{\"body\":\"Invalid comment\"}}" : commentJson(DOCUMENT);
            }
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            try (java.io.OutputStream output = exchange.getResponseBody()) {
                output.write(bytes);
            }
        });
        server.start();
        client = new JerseyJiraClient(baseUrl, "user", "token", "DEMO");
    }

    @After
    public void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void postsADocumentAndReadsBackTheCommentText() throws Exception {
        client.addComment("DEMO-1", new IssueComment("A new comment"));

        assertEquals("POST", requestMethod);
        assertDocumentBody();
        List<IssueComment> comments = client.getComments("DEMO-1");
        assertEquals(1, comments.size());
        assertEquals("A new comment", comments.get(0).getBody());
    }

    @Test
    public void updatesTheCommentUsingADocument() {
        writeStatus = 200;
        client.updateComment("DEMO-1", new IssueComment(baseUrl + "/rest/api/3/issue/DEMO-1/comment/1",
                1L, "A new comment", "user"));

        assertEquals("PUT", requestMethod);
        assertDocumentBody();
    }

    @Test
    public void reportsRejectedCommentCreation() {
        writeStatus = 400;
        try {
            client.addComment("DEMO-1", new IssueComment("A new comment"));
            fail("Expected rejected comment creation to fail");
        } catch (JQLException exception) {
            assertTrue(exception.getMessage().contains("400"));
            assertTrue(exception.getMessage().contains("Invalid comment"));
        }
    }

    @Test
    public void reportsRejectedCommentUpdates() {
        writeStatus = 400;
        try {
            client.updateComment("DEMO-1", new IssueComment(baseUrl + "/rest/api/3/issue/DEMO-1/comment/1",
                    1L, "A new comment", "user"));
            fail("Expected rejected comment update to fail");
        } catch (JQLException exception) {
            assertTrue(exception.getMessage().contains("400"));
            assertTrue(exception.getMessage().contains("Invalid comment"));
        }
    }

    @Test
    public void stillReadsLegacyStringBodies() throws Exception {
        assertEquals("Legacy comment", IssueComment.fromJsonString(commentJson("\"Legacy comment\"")).getBody());
    }

    @Test
    public void readsParagraphsAndHardBreaksAsPlainText() throws Exception {
        String body = "{\"type\":\"doc\",\"version\":1,\"content\":["
                + "{\"type\":\"paragraph\",\"content\":[{\"type\":\"text\",\"text\":\"First\"},"
                + "{\"type\":\"hardBreak\"},{\"type\":\"text\",\"text\":\"line\"}]},"
                + "{\"type\":\"paragraph\",\"content\":[]},"
                + "{\"type\":\"paragraph\",\"content\":[{\"type\":\"text\",\"text\":\"Last\"}]}]}";
        assertEquals("First\nline\n\nLast", IssueComment.fromJsonString(commentJson(body)).getBody());
    }

    private void assertDocumentBody() {
        JsonObject request = new JsonParser().parse(requestBody).getAsJsonObject();
        assertEquals(new JsonParser().parse(DOCUMENT), request.get("body"));
    }

    private String commentJson(String body) {
        return "{\"self\":\"" + baseUrl + "/rest/api/3/issue/DEMO-1/comment/1\",\"body\":" + body
                + ",\"author\":{\"self\":\"http://localhost/user\",\"accountId\":\"user\","
                + "\"displayName\":\"User\",\"active\":true},"
                + "\"created\":\"2026-01-01T12:00:00.000+0000\",\"updated\":\"2026-01-01T12:00:00.000+0000\"}";
    }
}
