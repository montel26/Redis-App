package org.mrwood26;

import java.io.IOException;
import java.net.Socket;

public interface ClientRequestHandler {
    void handleRequest(Socket clientSocket) throws IOException;
}
