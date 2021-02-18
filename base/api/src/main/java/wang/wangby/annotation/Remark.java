package wang.wangby.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//给字段,方法或类加备注
@Retention(RetentionPolicy.RUNTIME)
public @interface Remark {

	String value() default "";
}
