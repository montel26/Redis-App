package org.mrwood26.unittest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mrwood26.RDBPersistence.RDBPersistence;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RDBPersistenceTest {
    private static final String DIR = "/tmp/redis-files";
    private static final String DB_FILENAME = "dump.rdb";
    private RDBPersistence rdbPersistence;

    @BeforeEach
    public void setup() {
        rdbPersistence = new RDBPersistence(DIR, DB_FILENAME);
    }

    @Test
    public void testLoadRDBFile() throws IOException {
        // Assuming the RDB file exists and has valid data
        rdbPersistence.loadRDBFile();

        Map<String, String> store = rdbPersistence.getStore();
        Map<String, Long> expiryTimes = rdbPersistence.getExpiryTimes();

        // Check that keys are loaded correctly (replace "foo" and "bar" with actual keys in the dump.rdb)
        assertTrue(store.containsKey("foo"));
        assertTrue(store.containsKey("bar"));

        // Check that expiry times are loaded correctly
        assertNotNull(expiryTimes.get("foo"));
        assertNotNull(expiryTimes.get("bar"));
    }

    @Test
    public void testNoRDBFile() throws IOException {
        // Simulate the case where the RDB file does not exist
        File nonExistentFile = new File(DIR, "nonexistent.rdb");
        if (nonExistentFile.exists()) {
            nonExistentFile.delete();
        }

        rdbPersistence = new RDBPersistence(DIR, "nonexistent.rdb");
        rdbPersistence.loadRDBFile();

        Map<String, String> store = rdbPersistence.getStore();
        assertTrue(store.isEmpty(), "Store should be empty if the RDB file doesn't exist.");
    }
}

