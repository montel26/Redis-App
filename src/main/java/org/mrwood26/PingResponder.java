package org.mrwood26;

import java.io.IOException;
import java.net.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class PingResponder implements ClientRequestHandler {

    @Override
    public void handleRequest(Socket clientSocket) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream();

            System.out.println("Reading input from client...");
            String line;

            // Read the first line, which should be *1 (array of one element in Redis protocol)
            line = reader.readLine();
            System.out.println("Received: " + line);
            if (line != null && line.equals("*1")) {
                // Read the second line, which should be $4 (indicating the length of the command)
                line = reader.readLine();
                System.out.println("Received: " + line);
                if (line != null && line.equals("$4")) {
                    // Read the third line, which should be the actual command (PING)
                    line = reader.readLine();
                    System.out.println("Received: " + line);
                    if (line != null && line.equals("PING")) {
                        System.out.println("Responding with PONG...");
                        out.write("+PONG\r\n".getBytes());
                        out.flush();
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("IOException in PingResponder: " + e.getMessage());
        }
    }
}

