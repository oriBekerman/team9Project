package il.cshaifasweng.OCSFMediatorExample.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


//a bidirectional map  allows lookup of key->value and value->key
public class BiMap<K, V> {
    private final Map<K, V> forward = new HashMap<>();
    private final Map<V, K> reverse = new HashMap<>();

    public void put(K key, V value) {
        forward.put(key, value);
        reverse.put(value, key);
    }

    public V getValue(K key) {
        return forward.get(key);
    }

    public K getKey(V value) {
        return reverse.get(value);
    }

    public void removeByKey(K key) {
        V value = forward.remove(key);
        if (value != null) reverse.remove(value);
    }

    public void removeByValue(V value) {
        K key = reverse.remove(value);
        if (key != null) forward.remove(key);
    }

    public boolean containsKey(K key) {
        return forward.containsKey(key);
    }

    public boolean containsValue(V value) {
        return reverse.containsKey(value);
    }

    public Set<K> keySet() {
        return forward.keySet();
    }

    public Collection<V> values() {
        return forward.values();
    }
}

