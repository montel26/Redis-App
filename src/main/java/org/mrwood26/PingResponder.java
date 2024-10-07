package org.mrwood26;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class PingResponder implements ClientRequestHandler {
    private final Map<String, String> store;
    private final Map<String, Long> expiryTimes;
    private final String role; // Either "master" or "slave"
    private final String masterReplId; // 40-character replication ID
    private final long masterReplOffset; // Replication offset

    public PingResponder(Map<String, String> store, Map<String, Long> expiryTimes, String role, String masterReplId, long masterReplOffset) {
        this.store = store;
        this.expiryTimes = expiryTimes;
        this.role = role;  // Initialize with role
        this.masterReplId = masterReplId;  // Initialize with master replication ID
        this.masterReplOffset = masterReplOffset;  // Initialize with replication offset
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

                if (command.equals("INFO") && arguments.length == 2 && arguments[1].equalsIgnoreCase("replication")) {
                    // Respond to INFO replication command with role, masterReplId, and masterReplOffset
                    String infoResponse = String.format("role:%s\r\nmaster_replid:%s\r\nmaster_repl_offset:%d\r\n", role, masterReplId, masterReplOffset);
                    String bulkStringResponse = String.format("$%d\r\n%s", infoResponse.length(), infoResponse);
                    out.write(bulkStringResponse.getBytes());
                    out.flush();
                } else if (command.equals("GET") && arguments.length == 2) {
                    // Handle GET command
                    String key = arguments[1];
                    String value = store.get(key);

                    if (value != null && !isExpired(key)) {
                        out.write(String.format("$%d\r\n%s\r\n", value.length(), value).getBytes());
                    } else {
                        out.write("$-1\r\n".getBytes());
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
        return expiryTime != null && System.currentTimeMillis() > expiryTime;
    }
}
