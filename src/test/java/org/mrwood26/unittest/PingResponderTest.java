package org.mrwood26.unittest;

import org.junit.Before;
import org.junit.Test;
import org.mrwood26.PingResponder;
import org.mrwood26.ClientRequestHandler;

import java.io.*;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PingResponderTest {
    private Socket mockSocket;
    private ClientRequestHandler pingResponder;

    @Before
    public void setUp() throws IOException {
        mockSocket = mock(Socket.class);  // Using Mockito's mock method
        pingResponder = new PingResponder();

        // Set up input and output streams for the mock socket
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream("*1\r\n$4\r\nPING\r\n".getBytes()));
        when(mockSocket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
    }

    @Test
    public void testPingResponderHandlesPing() throws IOException {
        // Call the request handler
        pingResponder.handleRequest(mockSocket);

        // Capture the output
        OutputStream outputStream = mockSocket.getOutputStream();
        String output = outputStream.toString();

        // Validate response
        assertEquals("+PONG\r\n", output.trim());
    }

    @Test
    public void testPingResponderHandlesEcho() throws IOException {
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream("*2\r\n$4\r\nECHO\r\n$5\r\nhello\r\n".getBytes())) ;

        pingResponder.handleRequest(mockSocket);

        OutputStream outputStream = mockSocket.getOutputStream();
        String output = outputStream.toString();

        assertEquals("$5\r\nhello\r\n", output.trim());
    }
}


