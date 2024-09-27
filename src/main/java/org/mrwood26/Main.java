package org.mrwood26;

import org.mrwood26.RDBPersistence.RDBPersistence;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String dir = "/tmp/redis-files"; // Change this based on --dir arg
        String dbfilename = "dump.rdb";  // Change this based on --dbfilename arg

        RDBPersistence rdbPersistence = new RDBPersistence(dir, dbfilename);
        try {
            rdbPersistence.loadRDBFile();
        } catch (IOException e) {
            System.out.println("Error loading RDB file: " + e.getMessage());
        }

        // Pass the dir and dbfilename to the TCPServer constructor
        ClientRequestHandler requestHandler = new PingResponder(rdbPersistence.getStore());
        TCPServer tcpServer = new TCPServer(6379, dir, dbfilename, requestHandler);  // Updated constructor

        tcpServer.start();
    }
}




