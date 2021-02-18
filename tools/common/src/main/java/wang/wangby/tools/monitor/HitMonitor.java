package wang.wangby.tools.monitor;

import lombok.Getter;
import wang.wangby.tools.monitor.dto.HitInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//命中率监控
public class HitMonitor<Key> implements MonitorData{
    @Getter
    HitInfo<String> total;
    private Map<Key, HitInfo<Key>> dataMap = new ConcurrentHashMap<>();
    @Getter
    private String name;

    public HitMonitor(String name) {
        total = new HitInfo();
        total.setKey("total");
        total.setCreateTime(System.currentTimeMillis());
        this.name = "HitMonitor_"+name;
    }


    public void miss(Key key) {
        HitInfo info = getOrCreate(key);
        info.getMiss().incrementAndGet();
        total.getMiss().incrementAndGet();
    }

    public void hit(Key key) {
        HitInfo info = getOrCreate(key);
        info.getHit().incrementAndGet();
        total.getHit().incrementAndGet();
    }

    private HitInfo<Key> getOrCreate(Key key) {
        HitInfo<Key> info = dataMap.get(key);
        if (info == null) {
            synchronized (this) {
                info = dataMap.get(key);
                if (info != null) {
                    return info;
                }
                info = new HitInfo<>();
                info.setKey(key);
                info.setCreateTime(System.currentTimeMillis());
                dataMap.put(key,info);
            }
        }
        return info;
    }

    public void create(Key key) {
        getOrCreate(key);
    }

    @Override
    public Map<Object, Object> getDataMap() {
        Map map=new HashMap();
        map.putAll(dataMap);
        map.put(name+"_total",total);
        return map;
    }
}
