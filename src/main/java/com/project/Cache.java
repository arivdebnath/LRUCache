package com.project;

public interface Cache<K,V> {

    // get, put, invalidate, contains

    public V get(K key);

    public void put(K key, V value, long ttl); // ttl in milliseconds;

    public boolean contains(K key);

    public boolean invalidate(K key); // deletes or expires the cache entry

    public void close();

}
