package com.project;


public class Node<K, V>{
    private final K key;
    private V value;
    private Node<K,V> prevNode;
    private Node<K,V> nextNode;
    private long ttl;

    public Node(K key, V value, long ttl){
        this.key = key;
        this.value = value;
        this.ttl = ttl;
        this.prevNode = null;
        this.nextNode = null;
    }

    public K getKey(){
        return key;
    }

    public V getValue(){
        return value;
    }

    public Node<K, V> getPrevNode(){
        return prevNode;
    }

    public Node<K, V> getNextNode(){
        return nextNode;
    }

    public void setValue(V value){
        this.value = value;
    }

    public void setPrevNode(Node<K,V> prevNode){
        this.prevNode = prevNode;
    }

    public void setNextNode(Node<K,V> nextNode){
        this.nextNode = nextNode;
    }

}
