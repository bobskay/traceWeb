package wang.wangby.tools.monitor.dto;

import lombok.Data;
import wang.wangby.annotation.Remark;

import java.util.concurrent.atomic.AtomicLong;

@Data
@Remark("缓存命中情况")
public class HitInfo<T> {
    @Remark("创建时间")
    private long createTime=System.currentTimeMillis();
    @Remark("命中次数")
    private AtomicLong hit=new AtomicLong(0);
    @Remark("未命中次数")
    private AtomicLong miss=new AtomicLong(0);
    @Remark("唯一标识")
    private T key;
}
