package org.mrwood26.unittest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mrwood26.TCPServer;
import org.mrwood26.PingResponder;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class MultiCommandIntegrationTest {
    private TCPServer tcpServer;
    private ExecutorService serverExecutor;
    private Thread serverThread;

    @Before
    public void setUp() throws InterruptedException {
        tcpServer = new TCPServer(6379, new PingResponder());

        // Start the server in a new thread
        serverExecutor = Executors.newSingleThreadExecutor();
        serverThread = new Thread(() -> tcpServer.start());
        serverExecutor.submit(serverThread);

        // Give the server time to start
        TimeUnit.SECONDS.sleep(2);
    }

    @After
    public void tearDown() throws InterruptedException {
        // Stop the server
        tcpServer.stop();
        serverThread.interrupt(); // Interrupt the server thread if needed

        serverExecutor.shutdown();
        serverExecutor.awaitTermination(2, TimeUnit.SECONDS); // Wait for server to stop
    }

// TODO fIX TEST ITS ANNOYING


//    @Test
//    public void testMultiplePingCommandsOnSameConnection() throws IOException {
//        // Connect to the server
//        try (Socket clientSocket = new Socket("localhost", 6379)) {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            OutputStream out = clientSocket.getOutputStream();
//
//            // Send multiple PING commands
//            out.write("*1\r\n$4\r\nPING\r\n".getBytes());
//            out.flush();
//            assertEquals("+PONG", reader.readLine());
//
//            out.write("*1\r\n$4\r\nPING\r\n".getBytes());
//            out.flush();
//            assertEquals("+PONG", reader.readLine());
//        }
//    }
}

