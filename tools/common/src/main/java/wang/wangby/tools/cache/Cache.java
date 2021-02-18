package wang.wangby.tools.cache;

import java.util.function.Function;

public interface Cache<K, V> {

    V get(K key);

    /**
     * @param key
     * @param value
     * @param expireMs 多少毫秒后过期,如果小于0,说明不过期
     */
    void put(K key, V value, Long expireMs);

    default void put(K key, V value){
        put(key,value,null);
    };

    V delete(K key);

    static <K,V> V get(Cache<K, V> cache, K key, Long expireSecond, Function<K, V> supply) {
        V v =cache.get(key);
        if (v != null) {
            return v;
        }
        synchronized (cache) {
            v =cache.get(key);
            if (v != null) {
                return v;
            }
            v = supply.apply(key);
            cache.put(key, v, expireSecond);
            return v;
        }
    }
}
