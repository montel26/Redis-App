package org.mrwood26.RDBPersistence;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RDBPersistence {
    private String dir;
    private String dbfilename;
    private Map<String, String> store = new HashMap<>();
    private Map<String, Long> expiryTimes = new HashMap<>(); // Map to store expiry times for each key

    public RDBPersistence(String dir, String dbfilename) {
        this.dir = dir;
        this.dbfilename = dbfilename;
    }

    // Method to load the RDB file and store multiple keys/values with expiry times
    public void loadRDBFile() throws IOException {
        File file = new File(dir, dbfilename);
        if (!file.exists()) {
            System.out.println("RDB file does not exist. Skipping load.");
            return;
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            while (dis.available() > 0) {
                int keyLength = dis.readUnsignedByte();  // Read key length
                byte[] keyBytes = new byte[keyLength];
                dis.readFully(keyBytes);
                String key = new String(keyBytes);

                int valueLength = dis.readUnsignedByte();  // Read value length
                byte[] valueBytes = new byte[valueLength];
                dis.readFully(valueBytes);
                String value = new String(valueBytes);

                // Read expiry timestamp (if any)
                long expiryTime = -1;
                if (dis.available() > 0) {
                    expiryTime = dis.readLong();  // Read expiry time as Unix timestamp
                }

                // Store the key-value pair and expiry time
                store.put(key, value);
                if (expiryTime > 0) {
                    expiryTimes.put(key, expiryTime);
                }
            }
        }
    }

    public Map<String, String> getStore() {
        return store;
    }

    public Map<String, Long> getExpiryTimes() {
        return expiryTimes;
    }
}
