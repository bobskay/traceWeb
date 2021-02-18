package wang.wangby.annotation.api;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Params.class)
public @interface Param {

	// 描述
	String value();

	// 参数名
	String name() default "";
	
	//当前参数是否能为空
	boolean notNull() default false;
	
	//哪些字段忽略
	String[] ignores() default {} ;

	//哪些字段不能为空
	String[] notNulls() default {} ;

	
}
