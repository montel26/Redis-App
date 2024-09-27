package org.mrwood26.RDBPersistence;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RDBPersistence {
    private String dir;
    private String dbfilename;
    private Map<String, String> store = new HashMap<>();

    public RDBPersistence(String dir, String dbfilename) {
        this.dir = dir;
        this.dbfilename = dbfilename;
    }

    // Method to load the RDB file
    public void loadRDBFile() throws IOException {
        File file = new File(dir, dbfilename);
        if (!file.exists()) {
            System.out.println("RDB file does not exist. Skipping load.");
            return;
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            // Example of parsing length-prefixed strings (not full RDB format)
            int keyLength = dis.readUnsignedByte();  // Read key length
            byte[] keyBytes = new byte[keyLength];
            dis.readFully(keyBytes);
            String key = new String(keyBytes);

            int valueLength = dis.readUnsignedByte();  // Read value length
            byte[] valueBytes = new byte[valueLength];
            dis.readFully(valueBytes);
            String value = new String(valueBytes);

            store.put(key, value);  // Store in the in-memory map
        }
    }

    public Map<String, String> getStore() {
        return store;
    }
}

