package org.mrwood26;

import org.mrwood26.RDBPersistence.RDBPersistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        String dir = "/tmp/redis-files";  // Default directory
        String dbfilename = "dump.rdb";   // Default RDB filename
        String role = "master";           // Default role is master
        String masterHost = null;         // Host of the master (if replica)
        int masterPort = -1;              // Port of the master (if replica)
        int replicaPort = 6379;           // Port where the replica will listen
        String masterReplId = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb"; // Hardcoded master replid
        long masterReplOffset = 0;        // Initial replication offset is 0

        // Parse command-line arguments
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--dir") && i + 1 < args.length) {
                dir = args[i + 1];
            } else if (args[i].equals("--dbfilename") && i + 1 < args.length) {
                dbfilename = args[i + 1];
            } else if (args[i].equals("--replicaof") && i + 2 < args.length) {
                role = "slave"; // Set role to slave
                masterHost = args[i + 1];
                masterPort = Integer.parseInt(args[i + 2]);
            } else if (args[i].equals("--port") && i + 1 < args.length) {
                replicaPort = Integer.parseInt(args[i + 1]);
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

        // Initialize the PingResponder with the role (master or slave), masterReplId, and masterReplOffset
        ClientRequestHandler requestHandler = new PingResponder(rdbPersistence.getStore(), rdbPersistence.getExpiryTimes(), role, masterReplId, masterReplOffset);

        // Initialize and start the TCP server for client requests
        TCPServer tcpServer = new TCPServer(replicaPort, requestHandler);
        tcpServer.start();

        // If in replica mode, initiate the handshake with the master
        if (role.equals("slave")) {
            initiateReplicaHandshake(masterHost, masterPort, replicaPort);
        }

        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down the server...");
            tcpServer.stop();
        }));
    }

    /**
     * Initiates the handshake with the master by sending a PING, two REPLCONF commands, and a PSYNC command.
     */
    private static void initiateReplicaHandshake(String masterHost, int masterPort, int replicaPort) {
        try {
            System.out.println("Connecting to master at " + masterHost + ":" + masterPort);
            Socket masterSocket = new Socket(masterHost, masterPort);

            OutputStream outputStream = masterSocket.getOutputStream();
            InputStream inputStream = masterSocket.getInputStream();

            // Step 1: Send PING command
            String pingCommand = "*1\r\n$4\r\nPING\r\n";
            outputStream.write(pingCommand.getBytes());
            outputStream.flush();

            // Read response to PING
            readMasterResponse(inputStream);

            // Step 2: Send REPLCONF listening-port <PORT> command
            String replconfListeningPortCommand = String.format(
                    "*3\r\n$8\r\nREPLCONF\r\n$14\r\nlistening-port\r\n$%d\r\n%d\r\n",
                    String.valueOf(replicaPort).length(),
                    replicaPort
            );
            outputStream.write(replconfListeningPortCommand.getBytes());
            outputStream.flush();

            // Read response to REPLCONF listening-port
            readMasterResponse(inputStream);

            // Step 3: Send REPLCONF capa psync2 command
            String replconfCapaCommand = "*3\r\n$8\r\nREPLCONF\r\n$4\r\ncapa\r\n$6\r\npsync2\r\n";
            outputStream.write(replconfCapaCommand.getBytes());
            outputStream.flush();

            // Read response to REPLCONF capa psync2
            readMasterResponse(inputStream);

            // Step 4: Send PSYNC ? -1 command (to request full resynchronization)
            String psyncCommand = "*3\r\n$5\r\nPSYNC\r\n$1\r\n?\r\n$2\r\n-1\r\n";
            outputStream.write(psyncCommand.getBytes());
            outputStream.flush();

            // Read response to PSYNC (for now, we just print it, but we'll handle it in future stages)
            readMasterResponse(inputStream);

            System.out.println("Handshake complete with master.");

        } catch (IOException e) {
            System.err.println("Error during handshake with master: " + e.getMessage());
        }
    }

    /**
     * Reads the master's response (for example, +OK or +FULLRESYNC).
     */
    private static void readMasterResponse(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        if (bytesRead != -1) {
            String response = new String(buffer, 0, bytesRead);
            System.out.println("Master responded: " + response.trim());
        }
    }
}
