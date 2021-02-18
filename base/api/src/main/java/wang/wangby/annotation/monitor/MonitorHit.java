package wang.wangby.annotation.monitor;

import wang.wangby.annotation.Remark;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//根据方法返回值是否是null监控命中率
@Retention(RetentionPolicy.RUNTIME)
@Remark
public @interface MonitorHit {
    int value() default 0 ;
}
