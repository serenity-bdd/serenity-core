package api;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/** A tiny stand-in for the application under test, so the example runs without a network. */
public class BookshopServer {
    private HttpServer server;

    public int start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/api/books", exchange -> {
            String path = exchange.getRequestURI().getPath();
            int status = 200;
            String body;
            if (path.equals("/api/books")) {
                body = "[{\"id\":1,\"title\":\"Fluent Python\",\"price\":45.0},{\"id\":2,\"title\":\"Clean Code\",\"price\":33.5}]";
            } else if (path.equals("/api/books/2")) {
                body = "{\"id\":2,\"title\":\"Clean Code\",\"price\":33.5}";
            } else {
                status = 404;
                body = "{\"error\":\"Book not found\"}";
            }
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.createContext("/api/orders", exchange -> {
            boolean signedIn = "Bearer abc123".equals(exchange.getRequestHeaders().getFirst("Authorization"));
            int status = signedIn ? 201 : 401;
            String body = signedIn ? "{\"id\":\"ORD-1001\",\"total\":90.0,\"status\":\"PLACED\"}" : "{\"error\":\"Authentication required\"}";
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        return server.getAddress().getPort();
    }

    public void stop() {
        server.stop(0);
    }
}
