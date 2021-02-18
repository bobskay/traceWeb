package wang.wangby.tools.cache;

import lombok.Getter;
import wang.wangby.annotation.Remark;

public class CacheData<T> {

    @Remark("过期时间")
    @Getter
    private long expireMs;
    @Remark("最后更新时间")
    @Getter
    private long lastUpdate;
    @Remark("最后读取时间")
    @Getter
    private long lastRead;
    @Remark("数据")
    private T data;

    public CacheData(T data,long expireMs){
        this.expireMs=expireMs;
        this.data=data;
        lastUpdate=System.currentTimeMillis();
    }

    public void update(T data){
        this.data=data;
        lastUpdate=System.currentTimeMillis();
    }

    public T get(){
        lastRead=System.currentTimeMillis();
        return data;
    }


    //数据是否过期 @param read 如果长时间未读也算过期
    public boolean isExpire() {
        if(expireMs<0){
            return false;
        }
        return System.currentTimeMillis()-lastUpdate>expireMs;
    }

}
