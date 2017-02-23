package net.serenitybdd.rest.configuring

class NextAvailablePort {
    static int number() {
        ServerSocket serverSocket = new ServerSocket(0);
        return serverSocket.getLocalPort();
    }
}
