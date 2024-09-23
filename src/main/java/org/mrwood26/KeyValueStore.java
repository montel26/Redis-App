package org.mrwood26;

import java.util.HashMap;
import java.util.Map;

public class KeyValueStore {
    private final Map<String, String> store;
    private final Map<String, Long> expiryTimes; // Store expiry times for each key

    public KeyValueStore() {
        this.store = new HashMap<>();
        this.expiryTimes = new HashMap<>();
    }

    public void set(String key, String value) {
        store.put(key, value);
        expiryTimes.remove(key); // Remove any existing expiry when setting a new value
    }

    public void setWithExpiry(String key, String value, long expiryMillis) {
        store.put(key, value);
        expiryTimes.put(key, System.currentTimeMillis() + expiryMillis); // Store the expiry time
    }

    public String get(String key) {
        if (isExpired(key)) {
            store.remove(key); // Remove the key if it's expired
            expiryTimes.remove(key);
            return null;
        }
        return store.get(key);
    }

    private boolean isExpired(String key) {
        Long expiryTime = expiryTimes.get(key);
        if (expiryTime == null) {
            return false; // No expiry set
        }
        return System.currentTimeMillis() > expiryTime;
    }
}

