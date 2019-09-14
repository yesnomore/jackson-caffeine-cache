package com.github.pjfanning.jackson;

import com.fasterxml.jackson.databind.util.LRUMap;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.ConcurrentMap;

public class CaffeineLookupCache<K, V> extends LRUMap<K, V> {

    private final ConcurrentMap<K, V> cache;
    private final int maxEntries;

    public CaffeineLookupCache(int maxEntries) {
        super(4, 10); //super class has its own storage (that we will not use)
        this.maxEntries = maxEntries;
        Cache<K, V> fullCache = Caffeine.newBuilder().maximumSize(maxEntries).build();
        this.cache = fullCache.asMap();
    }

    @Override
    public V put(K key, V value) {
        V currentValue = get(key);
        cache.put(key, value);
        return currentValue;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V currentValue = get(key);
        cache.putIfAbsent(key, value);
        return currentValue;
    }

    @Override
    public V get(Object key) {
        return cache.get(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    protected Object readResolve() {
        return new CaffeineLookupCache(maxEntries);
    }
}
