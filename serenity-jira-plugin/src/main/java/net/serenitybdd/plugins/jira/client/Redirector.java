package net.serenitybdd.plugins.jira.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

/**
 * A description goes here.
 * User: john
 * Date: 29/08/13
 * Time: 11:59 AM
 */
public class Redirector {

    private static final int REDIRECT_REQUEST = 302;
    private final String path;
    private final Client client;


    public static class RedirectorBuilder {
        private final String path;

        public RedirectorBuilder(String path) {
            this.path = path;
        }

        public Redirector usingClient(Client client) {
            return  new Redirector(path, client);
        }
    }

    public static RedirectorBuilder forPath(String path) {
        return new RedirectorBuilder(path);
    }

    private Redirector(String path, Client client) {
        this.path = path;
        this.client = client;
    }

    public Response followRedirectsIn(Response response) {
        while (response.getStatus() == REDIRECT_REQUEST) {
            response = client.target(response.getLocation()).path(path).request().get();
        }
        return response;
    }
}
