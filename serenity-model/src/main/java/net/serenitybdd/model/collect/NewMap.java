package net.serenitybdd.model.collect;

import java.util.HashMap;
import java.util.Map;

public class NewMap {

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap();
    }

    public static <K, V> Map<K, V> copyOf(Map<? extends K, ? extends V> map) {
        return new HashMap(map);
    }

    public static <K, V> Map<K, V> of(K k1, V v1) {
        HashMap map = new HashMap();
        map.put(k1,v1);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
        HashMap map = new HashMap();
        map.put(k1,v1);
        map.put(k2,v2);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        HashMap map = new HashMap();
        map.put(k1,v1);
        map.put(k2,v2);
        map.put(k3,v3);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        HashMap map = new HashMap();
        map.put(k1,v1);
        map.put(k2,v2);
        map.put(k3,v3);
        map.put(k4,v4);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        HashMap map = new HashMap();
        map.put(k1,v1);
        map.put(k2,v2);
        map.put(k3,v3);
        map.put(k4,v4);
        map.put(k5,v5);
        return map;
    }

}
