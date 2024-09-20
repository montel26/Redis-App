package org.mrwood26;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class TCPServer {
    private int port;
    private ClientRequestHandler requestHandler;
    private volatile boolean running = true; // Flag to control server running state

    public TCPServer(int port, ClientRequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            System.out.println("Server started. Listening on port " + port);

            while (running) {
                try {
                    System.out.println("Waiting for client connection...");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected!");

                    // Handle each client in a separate thread
                    ClientHandler clientHandler = new ClientHandler(clientSocket, requestHandler);
                    new Thread(clientHandler).start();
                } catch (SocketTimeoutException e) {
                    // Handle timeout if needed
                }
            }
        } catch (IOException e) {
            System.out.println("IOException in TCPServer: " + e.getMessage());
        }
    }

    public void stop() {
        running = false; // Set running to false to exit the loop
    }
}



