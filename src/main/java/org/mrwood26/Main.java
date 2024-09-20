package org.mrwood26;

public class Main {
    public static void main(String[] args) {
        ClientRequestHandler pingResponder = new PingResponder();
        TCPServer tcpServer = new TCPServer(6379, pingResponder);

        tcpServer.start();
    }
}


