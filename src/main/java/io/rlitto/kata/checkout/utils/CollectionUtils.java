package io.rlitto.kata.checkout.utils;

import java.util.*;

import static java.util.Collections.*;

public class CollectionUtils {
    
    public static <K, V> Map<K, V> immutableCopyOf(Map<K, V> map){
        return map == null ? emptyMap() : unmodifiableMap(new HashMap<>(map));
    }

    public static <E> Set<E> immutableCopyOf(Set<E> set){
        return set == null ? emptySet() : unmodifiableSet(new HashSet<>(set));
    }

    public static <E> List<E> immutableCopyOf(List<E> list){
        return list == null ? emptyList() : unmodifiableList(new ArrayList<>(list));
    }
}
