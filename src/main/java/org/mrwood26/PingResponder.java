package org.mrwood26;
import java.io.*;
import java.net.Socket;
import java.util.Map;

public class PingResponder implements ClientRequestHandler {
    private final Map<String, String> store;
    private final Map<String, Long> expiryTimes;

    public PingResponder(Map<String, String> store, Map<String, Long> expiryTimes) {
        this.store = store;
        this.expiryTimes = expiryTimes;
    }

    @Override
    public void handleRequest(Socket clientSocket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        OutputStream out = clientSocket.getOutputStream();
        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println("Received: " + line);

            if (line.startsWith("*")) {
                int numArgs = Integer.parseInt(line.substring(1));
                String[] arguments = new String[numArgs];

                for (int i = 0; i < numArgs; i++) {
                    reader.readLine();  // Ignore argument length
                    arguments[i] = reader.readLine();
                }

                String command = arguments[0].toUpperCase();
                if (command.equals("GET") && arguments.length == 2) {
                    String key = arguments[1];
                    String value = store.get(key);

                    // Check if the key has expired
                    if (value != null && !isExpired(key)) {
                        out.write(String.format("$%d\r\n%s\r\n", value.length(), value).getBytes());
                    } else {
                        out.write("$-1\r\n".getBytes());  // Null bulk reply if key is expired or doesn't exist
                    }
                    out.flush();
                } else {
                    out.write("-ERR unknown command\r\n".getBytes());
                    out.flush();
                }
            }
        }
    }

    private boolean isExpired(String key) {
        Long expiryTime = expiryTimes.get(key);
        if (expiryTime == null) {
            return false; // No expiry set
        }
        return System.currentTimeMillis() > expiryTime;
    }
}
