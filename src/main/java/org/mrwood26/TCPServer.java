package org.mrwood26;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private int port;
    private ClientRequestHandler requestHandler;

    public TCPServer(int port, ClientRequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
    }

    public void start() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            System.out.println("Server started. Listening on port " + port);

            while (true) {
                System.out.println("Waiting for client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");

                // Handle each client in a separate thread
                ClientHandler clientHandler = new ClientHandler(clientSocket, requestHandler);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();  // Start the thread to handle the client
            }
        } catch (IOException e) {
            System.out.println("IOException in TCPServer: " + e.getMessage());
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println("IOException while closing server socket: " + e.getMessage());
                }
            }
        }
    }
}



