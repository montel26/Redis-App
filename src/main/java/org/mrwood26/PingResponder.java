package org.mrwood26;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class PingResponder implements ClientRequestHandler {
    private final KeyValueStore keyValueStore;

    public PingResponder() {
        this.keyValueStore = new KeyValueStore(); // Initialize the key-value store
    }

    @Override
    public void handleRequest(Socket clientSocket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        OutputStream out = clientSocket.getOutputStream();
        String line;

        // Keep reading commands from the same connection until it is closed
        while ((line = reader.readLine()) != null) {
            System.out.println("Received: " + line);

            // Check for the correct Redis protocol format
            if (line.startsWith("*")) {
                int numArgs = Integer.parseInt(line.substring(1));
                String[] arguments = new String[numArgs];

                for (int i = 0; i < numArgs; i++) {
                    reader.readLine(); // Read the length of argument (ignore this part)
                    arguments[i] = reader.readLine(); // Read the actual argument
                }

                String command = arguments[0].toUpperCase();
                switch (command) {
                    case "PING":
                        System.out.println("Responding with PONG...");
                        out.write("+PONG\r\n".getBytes());
                        out.flush();
                        break;

                    case "ECHO":
                        handleEchoCommand(arguments, out);
                        break;

                    case "SET":
                        handleSetCommand(arguments, out);
                        break;

                    case "GET":
                        handleGetCommand(arguments, out);
                        break;

                    default:
                        out.write("-ERROR unknown command\r\n".getBytes());
                        out.flush();
                        break;
                }
            }
        }

        // When the loop breaks, it means the client disconnected
        System.out.println("Client disconnected.");
    }

    // Method to handle the ECHO command
    private void handleEchoCommand(String[] arguments, OutputStream out) throws IOException {
        if (arguments.length >= 1) { // ECHO expects exactly one argument
            String message = arguments[1];
            System.out.println(message);
            System.out.println("Responding with ECHO: " + message);
            out.write(String.format("$%d\r\n%s\r\n", message.length(), message).getBytes());
        } else {
            out.write("-ERROR wrong number of arguments for ECHO\r\n".getBytes());
        }
        out.flush();
    }

    private void handleSetCommand(String[] arguments, OutputStream out) throws IOException {
        if (arguments.length >= 3) {
            String key = arguments[1];
            String value = arguments[2];
            long expiryMillis = 0; // Default no expiry

            // Check if there is an "PX" argument for expiry
            if (arguments.length == 5 && arguments[3].equalsIgnoreCase("PX")) {
                try {
                    expiryMillis = Long.parseLong(arguments[4]);
                } catch (NumberFormatException e) {
                    out.write("-ERROR invalid expiry time\r\n".getBytes());
                    out.flush();
                    return;
                }
                keyValueStore.setWithExpiry(key, value, expiryMillis);
                System.out.println("Stored key: " + key + " with value: " + value + " and expiry: " + expiryMillis + "ms");
            } else if (arguments.length == 3) {
                keyValueStore.set(key, value); // No expiry
                System.out.println("Stored key: " + key + " with value: " + value);
            } else {
                out.write("-ERROR invalid number of arguments for SET\r\n".getBytes());
                out.flush();
                return;
            }

            out.write("+OK\r\n".getBytes());
            out.flush();
        } else {
            out.write("-ERROR invalid number of arguments for SET\r\n".getBytes());
            out.flush();
        }
    }

    private void handleGetCommand(String[] arguments, OutputStream out) throws IOException {
        if (arguments.length == 2) {
            String key = arguments[1];
            String value = keyValueStore.get(key);
            if (value != null) {
                out.write(String.format("$%d\r\n%s\r\n", value.length(), value).getBytes());
            } else {
                out.write("$-1\r\n".getBytes()); // Null bulk reply for non-existent keys
            }
            out.flush();
        } else {
            out.write("-ERROR invalid number of arguments for GET\r\n".getBytes());
            out.flush();
        }
    }
}