package org.mrwood26;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private final Socket clientSocket;
    private final ClientRequestHandler requestHandler;

    public ClientHandler(Socket clientSocket, ClientRequestHandler requestHandler) {
        this.clientSocket = clientSocket;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        try (Socket socket = clientSocket) {
            // Use the request handler to handle the client's requests
            requestHandler.handleRequest(socket);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException while handling client request: " + e.getMessage(), e);
        }
    }
}
