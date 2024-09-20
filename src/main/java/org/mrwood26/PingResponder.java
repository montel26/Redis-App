package org.mrwood26;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class PingResponder implements ClientRequestHandler {

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
                        if (arguments.length > 1) {
                            String message = arguments[1];
                            System.out.println("Responding with ECHO: " + message);
                            out.write(String.format("$%d\r\n%s\r\n", message.length(), message).getBytes());
                            out.flush();
                        } else {
                            out.write("-ERROR no argument provided for ECHO\r\n".getBytes());
                            out.flush();
                        }
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
}

