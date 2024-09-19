package org.mrwood26;


public class Main {
    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");

        // Using SOLID principles to separate concerns
        ClientRequestHandler pingResponder = new PingResponder();
        TCPServer tcpServer = new TCPServer(6379, pingResponder);

        tcpServer.start();
    }
}


