package wang.wangby.annotation.monitor;

import wang.wangby.annotation.Remark;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//监控某个方法执行情况,总次数,成功次数,失败次数,平均耗时
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Remark
public @interface InvokeMonitor {

    Class<? extends MonitorNameCreateor> value() default  DefaultMonitorName.class ;
}
