package wang.wangby.annotation.monitor;

import wang.wangby.annotation.Remark;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//监控某个方法执行耗时
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Remark
public @interface TimeMonitor {
	public static final long[] DEFAULT_ACCURACY ={5L,10L,100L,500L,1000L,3000L,5000L,10000L};

	//监控项名称前缀
	String value() default "DEFAULT";

	//根据参数值获取监控项名称,默认叫name,如果找不到就用类+方法名
	String paramName() default "name";

	//用时间隔
	long[] accuracy() default  {5L,10L,100L,500L,1000L,3000L,5000L};
}
