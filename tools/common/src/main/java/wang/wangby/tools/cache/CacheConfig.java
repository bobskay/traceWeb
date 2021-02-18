package wang.wangby.tools.cache;

import lombok.Data;
import wang.wangby.annotation.Remark;

import java.util.concurrent.TimeUnit;

@Data
public class CacheConfig {
    @Remark("缓存名称,一个jvm内唯一")
    private String name;
    @Remark("扫描间隔")
    private int scanInterval =-1;
    @Remark("默认存活时间,-1表示用不过期")
    private int defaultSurvival=-1;
    @Remark("时间单位")
    private TimeUnit timeUnit=TimeUnit.SECONDS;

    public static CacheConfig getInstance(String name){
        CacheConfig cacheConfig=new CacheConfig();
        cacheConfig.setName(name);
        return  cacheConfig;
    }

    /**
     * 获取缓存
     * @param name 名称
     * @param survivalSecond 数据存活时间
     * */
    public static CacheConfig getInstance(String name, int scanInterval, int survivalSecond){
        CacheConfig cacheConfig=new CacheConfig();
        cacheConfig.setName(name);
        cacheConfig.setScanInterval(scanInterval);
        cacheConfig.setDefaultSurvival(survivalSecond);
        return  cacheConfig;
    }
}
