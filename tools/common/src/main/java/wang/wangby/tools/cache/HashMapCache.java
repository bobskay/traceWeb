package wang.wangby.tools.cache;

import lombok.extern.slf4j.Slf4j;
import wang.wangby.annotation.monitor.MonitorHit;
import wang.wangby.tools.monitor.MonitorData;
import wang.wangby.tools.thread.job.Job;
import wang.wangby.tools.thread.job.JobConfig;
import wang.wangby.utils.DateTime;
import wang.wangby.utils.Integers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用ConcurrentHashMap当缓存
 */
@Slf4j
@MonitorHit
public class HashMapCache<K, V> implements Cache<K, V>, Job, MonitorData {
    public Map<K, CacheData> cacheMap = new ConcurrentHashMap<>();

    private CacheConfig cacheConfig;

    public HashMapCache(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }


    public JobConfig jobConfig() {
        if (cacheConfig.getScanInterval() < 0) {
            log.info("永不过期缓存:" + cacheConfig);
            return null;
        }
        JobConfig jobConfig = new JobConfig();
        jobConfig.setName("cacheClearJob_" + cacheConfig.getName());
        jobConfig.setTimeUnit(cacheConfig.getTimeUnit());
        jobConfig.setDelay(cacheConfig.getScanInterval());
        jobConfig.setPeriod(jobConfig.getDelay());
        jobConfig.setRunnable(this::delete);
        return jobConfig;
    }

    //删除过期的key
    private void delete() {
        cacheMap.forEach((k, data) -> {
            if (data.isExpire()) {
                log.debug("删除过期数据:" + k);
                delete(k);
            }
        });
    }


    @Override
    @MonitorHit
    public V get(K key) {
        CacheData<V> data = cacheMap.get(key);
        if (data == null) {
            return null;
        }
        if (data.isExpire()) {
            cacheMap.remove(key);
            return null;
        }
        return data.get();
    }


    public void put(K key, V value, Long expMs) {
        if(expMs==null){
            expMs = cacheConfig.getTimeUnit().toMillis(cacheConfig.getDefaultSurvival());
        }
        CacheData data = new CacheData(value, expMs);
        cacheMap.put(key, data);
    }

    @Override
    public V delete(K key) {
        CacheData<V> d = cacheMap.remove(key);
        if (d != null) {
            return d.get();
        }
        return null;
    }


    public static HashMapCache createCache(CacheConfig cacheConfig) {
        return new HashMapCache(cacheConfig);
    }

    @Override
    public Map<Object, Object> getDataMap() {
        Map map = new HashMap();
        map.put("size", cacheMap.size());
        Integers i=new Integers(0);
        cacheMap.forEach((k, data) -> {
            if(i.incrementAndGet()<10){
                map.put(k,"lastUpdate= "+new DateTime(data.getLastUpdate())
                        +" ,lastRead= "+new DateTime(data.getLastRead())+" ,expire= "+data.getExpireMs()+"");
            }
        });
        return map;
    }

    public String getName() {
        return "hashMapCache_" + cacheConfig.getName();
    }


}
