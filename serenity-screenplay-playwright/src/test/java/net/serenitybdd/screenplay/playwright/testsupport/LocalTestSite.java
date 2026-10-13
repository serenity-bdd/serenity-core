package net.serenitybdd.screenplay.playwright.testsupport;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Serves the HTML fixtures in {@code src/test/resources/test-pages} over a local HTTP server,
 * so that tests do not depend on external sites. The server is started lazily on a random port
 * and shared by all tests in the JVM.
 *
 * Paths mirror the layout of the-internet.herokuapp.com (e.g. {@code /login}, {@code /dynamic_controls}).
 */
public final class LocalTestSite {

    private static final Map<String, String> ROUTES = Map.ofEntries(
        Map.entry("", "index.html"),
        Map.entry("dynamic_controls", "dynamic-controls.html"),
        Map.entry("dynamic_loading", "dynamic-loading.html"),
        Map.entry("dynamic_loading/1", "dynamic-loading.html"),
        Map.entry("dynamic_loading/2", "dynamic-loading.html"),
        Map.entry("status_codes/200", "status_200.html"),
        Map.entry("status_codes/404", "status_404.html")
    );

    private static HttpServer server;

    private LocalTestSite() {
    }

    /** Full URL for a path such as "/login" or "login"; "/" gives the home page. */
    public static synchronized String url(String path) {
        if (server == null) {
            start();
        }
        String clean = path.startsWith("/") ? path.substring(1) : path;
        return "http://localhost:" + server.getAddress().getPort() + "/" + clean;
    }

    private static void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        } catch (IOException e) {
            throw new IllegalStateException("Could not start local test site", e);
        }
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath().replaceAll("^/+|/+$", "");
            String resource = ROUTES.getOrDefault(path, path.endsWith(".html") ? path : path + ".html");
            int status = 200;
            if (path.equals("status_codes/404") || path.equals("status_404.html")) {
                status = 404;
                resource = "status_404.html";
            }
            byte[] body;
            try (InputStream in = LocalTestSite.class.getClassLoader().getResourceAsStream("test-pages/" + resource)) {
                if (in == null) {
                    status = 404;
                    body = "Not Found".getBytes(StandardCharsets.UTF_8);
                } else {
                    body = in.readAllBytes();
                }
            }
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(status, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "local-test-site");
            t.setDaemon(true);
            return t;
        }));
        server.start();
    }
}
