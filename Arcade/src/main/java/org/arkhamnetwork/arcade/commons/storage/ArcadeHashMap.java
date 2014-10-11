/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.storage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 *
 * @author devan_000
 * @param <KeyType>
 * @param <ValueType>
 */
public class ArcadeHashMap<KeyType, ValueType> {

    private ConcurrentHashMap<KeyType, ValueType> internalMap;

    public ArcadeHashMap() {
        super();
        this.internalMap = new ConcurrentHashMap<>();
    }

    public boolean containsKey(final KeyType key) {
        return this.internalMap.containsKey(key);
    }

    public boolean containsValue(final ValueType key) {
        return this.internalMap.containsValue(key);
    }

    public Set<Map.Entry<KeyType, ValueType>> entrySet() {
        return this.internalMap.entrySet();
    }

    public Set<KeyType> keySet() {
        return this.internalMap.keySet();
    }

    public Collection<ValueType> values() {
        return this.internalMap.values();
    }

    public ValueType get(final KeyType key) {
        return this.internalMap.get(key);
    }

    public ValueType remove(final KeyType key) {
        return this.internalMap.remove(key);
    }

    public ValueType put(final KeyType key, final ValueType value) {
        return this.internalMap.put(key, value);
    }

    public void clear() {
        this.internalMap.clear();
    }

    public int size() {
        return this.internalMap.size();
    }

    public boolean isEmpty() {
        return this.internalMap.isEmpty();
    }

}
