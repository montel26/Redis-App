package org.mrwood26;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private int port;
    private String dir;
    private String dbfilename;
    private ClientRequestHandler requestHandler;
    private volatile boolean running = true;

    public TCPServer(int port, String dir, String dbfilename, ClientRequestHandler requestHandler) {
        this.port = port;
        this.dir = dir;
        this.dbfilename = dbfilename;
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

                    ClientHandler clientHandler = new ClientHandler(clientSocket, requestHandler);
                    new Thread(clientHandler).start();
                } catch (IOException e) {
                    System.out.println("IOException in TCPServer: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("IOException in TCPServer: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
    }
}


