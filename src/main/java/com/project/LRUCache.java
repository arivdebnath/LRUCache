package com.project;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class LRUCache<K, V> implements Cache<K, V> {

    private Map<K, Node<K, V>> cacheMap;
    private ScheduledExecutorService cleanUpService;
    private ReentrantLock lock;
    private int capacity;
    private long defaultExpiration;

    private Node<K, V> head;
    private Node<K, V> tail;

    public LRUCache(int capacity, long defaultExpiration) {
        this.cacheMap = new HashMap<>();
        this.capacity = capacity;
        this.defaultExpiration = defaultExpiration;
        this.head = new Node<>(null, null, Integer.MAX_VALUE);
        this.tail = new Node<>(null, null, Integer.MAX_VALUE);
        head.setNextNode(tail);
        tail.setPrevNode(head);
        this.lock = new ReentrantLock();
        this.cleanUpService = Executors.newSingleThreadScheduledExecutor();
        this.cleanUpService.schedule(this::evictNodesBasedOnTtl, 60, TimeUnit.SECONDS);
    }


    @Override
    public V get(K key) {
        lock.lock();
        try {
            Node<K, V> node = cacheMap.get(key);
            if (node == null) {
                return null;
            }

            if (node.getExpiresAt() > 0 && node.getExpiresAt() <= System.currentTimeMillis()) {
                // expired
                Node<K, V> prev = node.getPrevNode();
                Node<K, V> next = node.getNextNode();
                prev.setNextNode(next);
                next.setPrevNode(prev);
                cacheMap.remove(key);
                return null;
            }
            moveToHead(node);
            return node.getValue();

        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(K key, V value, long ttl) {
        long expiresAt = System.currentTimeMillis() + ttl;
        lock.lock();
        try {
            if (!cacheMap.containsKey(key)) {
                Node<K, V> newNode = new Node<>(key, value, expiresAt);
                cacheMap.put(key, newNode);
                addNodeToHead(newNode);
            } else {
                Node<K, V> existingNode = cacheMap.get(key);
                existingNode.setValue(value);
                existingNode.setExpiresAt(expiresAt);
                cacheMap.get(key).setValue(value);
                moveToHead(existingNode);
            }
            removeNodesIfNecessary();
        } finally {
            lock.unlock();
        }

    }

    @Override
    public boolean contains(K key) {
        lock.lock();
        try {
            return cacheMap.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean invalidate(K key) {
        lock.lock();
        try{
            Node<K,V> node = cacheMap.get(key);
            if(node==null) return false;
            removeNode(node);
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        this.cleanUpService.shutdown();
    }

    private void addNodeToHead(Node<K, V> node) {
        if (head.getNextNode() == node) return;
        head.getNextNode().setPrevNode(node);
        node.setNextNode(head.getNextNode());
        node.setPrevNode(head);
        head.setNextNode(node);
    }

    private void moveToHead(Node<K, V> node) {
        Node<K, V> prev = node.getPrevNode();
        Node<K, V> next = node.getNextNode();
        prev.setNextNode(next);
        next.setPrevNode(prev);
        addNodeToHead(node);
    }

    private void removeNode(Node<K,V> node) {
        Node<K, V> prev = node.getPrevNode();
        Node<K, V> next = node.getNextNode();
        prev.setNextNode(next);
        next.setPrevNode(prev);
        node.setPrevNode(null);
        node.setNextNode(null);
        cacheMap.remove(node.getKey());
    }

    private void removeNodesIfNecessary() {

        int curCapacity = cacheMap.size();
        if (curCapacity > capacity) {
            while (cacheMap.size() > capacity) {
                if(tail.getPrevNode()==head) return;
                removeNode(tail.getPrevNode());
            }
        }

    }

    private void evictNodesBasedOnTtl() {
        Node<K, V> lastNode = tail.getPrevNode();
        while (lastNode.getExpiresAt() > 0 && lastNode.getExpiresAt() <= System.currentTimeMillis()) {
            if (lastNode == head) break;
            removeNode(lastNode);
            lastNode = tail.getPrevNode();
        }
    }

}
