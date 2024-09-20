package org.mrwood26;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    private int port;
    private ClientRequestHandler requestHandler;
    private ExecutorService threadPool; // Add thread pool

    public TCPServer(int port, ClientRequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
        this.threadPool = Executors.newFixedThreadPool(10); // Use a pool with a fixed size
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

                // Instead of creating a new thread directly, submit the task to the thread pool
                ClientHandler clientHandler = new ClientHandler(clientSocket, requestHandler);
                threadPool.submit(clientHandler);  // Use thread pool to handle the client
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
            threadPool.shutdown(); // Ensure proper shutdown of the thread pool
        }
    }
}



