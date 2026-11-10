package net.serenitybdd.plugins.jira.client;

import com.sun.net.httpserver.HttpServer;
import net.serenitybdd.plugins.jira.domain.IssueSummary;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class WhenReconcilingJiraSearches {
    private HttpServer server;
    private JerseyJiraClient client;
    private volatile String queryParameters;

    @Before
    public void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/rest/api/3/search/jql", exchange -> {
            queryParameters = exchange.getRequestURI().getQuery();
            boolean reconciled = queryParameters.contains("reconcileIssues=123");
            String issue = "{\"id\":\"123\",\"key\":\"DEMO-123\",\"self\":\"http://localhost/issue/123\","
                    + "\"renderedFields\":{},\"fields\":{\"summary\":\"New issue\","
                    + "\"issuetype\":{\"name\":\"Bug\"},\"status\":{\"name\":\"Open\"},"
                    + "\"labels\":[],\"fixVersions\":[],\"comment\":{\"comments\":[]}}}";
            byte[] body = ("{\"issues\":[" + (reconciled ? issue : "") + "],\"isLast\":true}")
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (java.io.OutputStream output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();
        client = new JerseyJiraClient("http://127.0.0.1:" + server.getAddress().getPort(),
                "user", "token", "DEMO");
    }

    @After
    public void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void reconcilesTheNumericIdDespiteAnEarlierCachedEmptyResult() {
        String query = "key=DEMO-123";
        assertTrue(client.findByJQL(query).isEmpty());
        assertFalse(queryParameters.contains("reconcileIssues"));

        List<IssueSummary> issues = client.findByJQLWithReconciliation(query, Collections.singletonList(123L));

        assertEquals(1, issues.size());
        assertEquals("DEMO-123", issues.get(0).getKey());
        assertTrue(queryParameters.contains("jql=key=DEMO-123"));
        assertTrue(queryParameters.contains("reconcileIssues=123"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsMoreThanFiftyReconciliationIds() {
        client.findByJQLWithReconciliation("project=DEMO", Collections.nCopies(51, 123L));
    }
}
