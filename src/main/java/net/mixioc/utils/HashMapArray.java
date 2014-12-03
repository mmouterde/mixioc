package net.mixioc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HashMapArray<K, V> {

    private final Map<K, ArrayList<V>> data;

    public HashMapArray() {
        data = new HashMap<K, ArrayList<V>>();
    }

    public HashMapArray(Map<K, ArrayList<V>> container) {
        data = container;
    }

    public Set<Entry<K, ArrayList<V>>> entrySet() {
        return data.entrySet();
    }

    public void initKeys(ArrayList<K> keys) {
        for (K key : keys) {
            initKey(key);
        }
    }

    public void initKey(K key) {
        data.put(key, new ArrayList<V>());
    }

    /**
     * @param key   add a value for this key
     * @param value the value to add
     */
    public void append(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("the key could not be null");
        }

        if (data.get(key) == null) {
            ArrayList<V> ar = new ArrayList<V>();
            ar.add(value);
            data.put(key, ar);
        } else {
            data.get(key).add(value);
        }
    }

    /**
     * @param key   add a value for this key
     * @param value the value to add
     */
    public void append(K key, ArrayList<V> value) {
        if (key == null) {
            throw new IllegalArgumentException("the key could not be null");
        }

        if (data.get(key) == null) {
            data.put(key, value);
        } else {
            data.get(key).addAll(value);
        }
    }

    /**
     * @param key the key to the list
     * @return the list matching with the key
     */
    public ArrayList<V> get(K key) {
        if (data.containsKey(key)) {
            return data.get(key);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @return the number of keys defined
     */
    public int size() {
        return data.size();
    }

    /**
     * @return the set of defined keys
     */
    public Set<K> keySet() {
        return data.keySet();
    }

    public ArrayList<V> remove(K key) {
        return data.remove(key);
    }

    /**
     * @return an Arraylist with All data
     */
    public ArrayList<V> values() {
        ArrayList<V> rst = new ArrayList<V>();
        for (ArrayList<V> toAdd : data.values()) {
            rst.addAll(toAdd);
        }
        return rst;
    }

    public void clear() {
        data.clear();
    }
}
