package org.mrwood26;

import org.mrwood26.RDBPersistence.RDBPersistence;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String dir = "/tmp/redis-files"; // Default directory
        String dbfilename = "dump.rdb";  // Default RDB filename

        // Parse command-line arguments for directory and filename
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--dir") && i + 1 < args.length) {
                dir = args[i + 1];
            } else if (args[i].equals("--dbfilename") && i + 1 < args.length) {
                dbfilename = args[i + 1];
            }
        }

        // Initialize RDBPersistence and load the RDB file
        RDBPersistence rdbPersistence = new RDBPersistence(dir, dbfilename);
        try {
            rdbPersistence.loadRDBFile();
        } catch (IOException e) {
            System.out.println("Error loading RDB file: " + e.getMessage());
            return;
        }

        // Initialize the PingResponder with the store and expiry times loaded from RDB
        ClientRequestHandler requestHandler = new PingResponder(rdbPersistence.getStore(), rdbPersistence.getExpiryTimes());

        // Initialize and start the TCP server
        TCPServer tcpServer = new TCPServer(6379, dir, dbfilename, requestHandler);
        tcpServer.start();

        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down the server...");
            tcpServer.stop();
        }));
    }
}
