package org.mrwood26;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ClientRequestHandler requestHandler;

    public ClientHandler(Socket clientSocket, ClientRequestHandler requestHandler) {
        this.clientSocket = clientSocket;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        try {
            // Use the request handler to handle the client's requests
            requestHandler.handleRequest(clientSocket);
        } catch (IOException e) {
            System.out.println("IOException in ClientHandler: " + e.getMessage());
        } finally {
            try {
                // Ensure the client socket is closed when done
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("IOException while closing client socket: " + e.getMessage());
            }
        }
    }
}
