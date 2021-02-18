package wang.wangby.utils;

import java.util.Map;
import java.util.function.Supplier;

public class CacheUtil {
    //通过duble check方式从map里获取数据
    public static <K,V> V get(Map<K,V> map, K key, Supplier<V> supplier,Object lock){
        V v=map.get(key);
        if(v!=null){
            return v;
        }
        synchronized (lock){
            v=map.get(key);
            if(v!=null){
                return v;
            }
            v=supplier.get();
            map.put(key,v);
            return v;
        }
    }
}
