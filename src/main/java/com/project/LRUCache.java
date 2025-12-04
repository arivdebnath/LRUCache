package com.project;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class LRUCache<K,V> implements Cache<K,V>{

    private Map<K, Node<K, V>> cacheMap;
    private long expirationTime;
    private ScheduledExecutorService cleanUpService;
    private ReentrantLock reentrantLock;

    private Node<K, V> head;



    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public void set(K key, V value) {

    }

    @Override
    public boolean contains(K key) {
        return false;
    }

    @Override
    public boolean invalidate(K key) {
        return false;
    }

    @Override
    public void close() {

    }
}
