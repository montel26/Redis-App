package org.mrwood26.unittest;

import org.junit.Before;
import org.junit.Test;
import org.mrwood26.TCPServer;
import org.mrwood26.ClientRequestHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;


public class TCPServerTest {
    private TCPServer tcpServer;
    private ClientRequestHandler mockRequestHandler;

    @Before
    public void setUp() {
        mockRequestHandler = (Socket clientSocket) -> {
        };
        tcpServer = new TCPServer(6379, mockRequestHandler);
    }

    @Test
    public void testServerStartsAndAcceptsConnections() throws IOException, InterruptedException {
        Executors.newSingleThreadExecutor().submit(() -> tcpServer.start());

        // Give the server time to start
        TimeUnit.SECONDS.sleep(2);

        // Simulate a client connection
        try (Socket clientSocket = new Socket("localhost", 6379)) {
            assertTrue(clientSocket.isConnected());
        }
    }
}
