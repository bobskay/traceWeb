package wang.wangby.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)   
@Target(ElementType.FIELD)
@Remark
public @interface Property {
	
	@AliasFor(annotation = Remark.class,attribute="value")
	String value();//备注
	int length() default 0;//字段长度
	boolean unique() default false;//是否唯一约束
	boolean pk() default false;//是否是主键
	String validate() default "";//完整性验证
	boolean dateOnly() default false;//是否只存日期,日期类型专用,
}
