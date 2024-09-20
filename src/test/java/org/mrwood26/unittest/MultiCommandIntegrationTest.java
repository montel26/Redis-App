package org.mrwood26.unittest;

import org.junit.Before;
import org.junit.Test;

import org.mrwood26.TCPServer;
import org.mrwood26.PingResponder;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;


public class MultiCommandIntegrationTest {
    private TCPServer tcpServer;

    @Before
    public void setUp() {
        tcpServer = new TCPServer(6379, new PingResponder());
    }


    @Test
    public void testMultiplePingCommandsOnSameConnection() throws InterruptedException, IOException {
        // Start the server in a new thread
        Executors.newSingleThreadExecutor().submit(() -> tcpServer.start());
        TimeUnit.SECONDS.sleep(2); // Give the server time to start

        // Connect to the server
        try (Socket clientSocket = new Socket("localhost", 6379)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream();

            // Send multiple PING commands
            out.write("*1\r\n$4\r\nPING\r\n".getBytes());
            out.flush();
            assertEquals("+PONG", reader.readLine());

            out.write("*1\r\n$4\r\nPING\r\n".getBytes());
            out.flush();
            assertEquals("+PONG", reader.readLine());
        }
    }
}

