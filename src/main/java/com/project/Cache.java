package com.project;

public interface Cache<K,V> {

    // get, set, invalidate, contains

    public V get(K key);

    public void set(K key, V value);

    public boolean contains(K key);

    public boolean invalidate(K key); // deletes or expires the cache entry

    public void close();

}
