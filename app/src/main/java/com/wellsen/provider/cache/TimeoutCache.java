package com.wellsen.provider.cache;

/**
 * This is common interface for all caches.
 */
public interface TimeoutCache<K, V> {
    /**
     * Gets the @a value from the cache at the designated @a key.
     * 
     * @param key   key
     * @return V
     */
    V get(K key);

    /**
     * Put the @a value into the cache at the designated @a key.
     * 
     * @param key   key
     * @param value value
     */
    void put(K key, V value);

    /**
     * Put the @a value into the cache at the designated @a key with a timeout
     * after which the data will expire
     * 
     * @param key   key
     * @param value value
     * @param timeout in seconds
     */
    void put(K key, V value, int timeout);

    /**
     * Removes the value associated with a key
     * 
     * @param key   key
     */
    void remove(K key);

    /**
     * Get the size of the cache.
     * 
     * @return size
     */
    int size();
}
