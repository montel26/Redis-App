package org.mrwood26.unittest;

import org.junit.Before;
import org.junit.Test;
import org.mrwood26.ClientHandler;
import org.mrwood26.ClientRequestHandler;
import org.easymock.EasyMock;

import java.io.IOException;
import java.net.Socket;

import static org.easymock.EasyMock.*;

public class ClientHandlerTest {
    private Socket mockSocket;
    private ClientRequestHandler mockRequestHandler;
    private ClientHandler clientHandler;

    @Before
    public void setUp() {
        // Create mock objects using EasyMock
        mockSocket = EasyMock.createMock(Socket.class);
        mockRequestHandler = EasyMock.createMock(ClientRequestHandler.class);
        clientHandler = new ClientHandler(mockSocket, mockRequestHandler);
    }

    @Test
    public void testHandleRequestIsCalled() throws IOException {
        // Set expectations for method calls
        mockRequestHandler.handleRequest(mockSocket);
        EasyMock.expectLastCall();

        // Replay mocks to prepare for testing
        EasyMock.replay(mockRequestHandler);

        clientHandler.run();

        // Verify that handleRequest was called exactly once
        EasyMock.verify(mockRequestHandler);
    }

    @Test
    public void testSocketIsClosedAfterRequest() throws IOException {
        // Set expectations for socket close
        mockSocket.close();
        EasyMock.expectLastCall();

        // Replay mocks to prepare for testing
        EasyMock.replay(mockSocket);

        clientHandler.run();

        // Verify that the socket was closed exactly once
        EasyMock.verify(mockSocket);
    }
}

