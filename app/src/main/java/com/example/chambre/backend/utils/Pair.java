package com.example.chambre.backend.utils;

import java.io.Serializable;

public class Pair<K, V> implements Serializable {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null){
            return false;
        }
        if (this == other){
            return true;
        }
        if (!(other instanceof Pair)){
            return false;
        }
        Pair o = (Pair) other;
        return (this.key==o.key) && (this.value == o.value);
    }
    public boolean equalKeys(Object other) {
        if (other == null){
            return false;
        }
        if (this == other){
            return true;
        }
        if (!(other instanceof Pair)){
            return false;
        }
        Pair o = (Pair) other;
        return (this.key==o.key);
    }



}
