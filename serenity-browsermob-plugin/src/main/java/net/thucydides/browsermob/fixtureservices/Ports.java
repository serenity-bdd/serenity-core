package net.thucydides.browsermob.fixtureservices;

import java.io.IOException;
import java.net.ServerSocket;

public class Ports {

    private static final int PORT_RANGE = 1000;
    private static final int MIN_AVAILABLE_PORT = 49152;
    private static final int MAX_AVAILABLE_PORT = MIN_AVAILABLE_PORT + PORT_RANGE;

    private final int defaultPort;

    public Ports(int defaultPort) {
        this.defaultPort = defaultPort;
    }

    public int nextAvailablePort(int portNumber) {
        if (portNumber > MAX_AVAILABLE_PORT) {
            throw new IllegalStateException("No available ports found");
        }
        if (isAvailable(portNumber)) {
            return portNumber;
        } else {
            return nextAvailablePort(portNumber + 1);
        }
    }

    public boolean isAvailable(int portNumber) {
        ServerSocket socket = null;
        boolean available = false;
        try {
            socket = new ServerSocket(portNumber);
            available = true;
        } catch (IOException e) {
            available = false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }
        return available;
    }

}
