package org.mrwood26;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private final int port;
    private final ClientRequestHandler requestHandler;
    private boolean running = true;

    public TCPServer(int port, ClientRequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            // Accept client connections while the server is running
            while (running) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket, requestHandler)).start();
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        System.out.println("Server is shutting down...");
    }
}

