package org.mrwood26;

public class Main {
    public static void main(String[] args) {
        ClientRequestHandler requestHandler = new PingResponder(); // Use PingResponder which handles SET, GET, PING
        TCPServer tcpServer = new TCPServer(6379, requestHandler);

        tcpServer.start();
    }
}


