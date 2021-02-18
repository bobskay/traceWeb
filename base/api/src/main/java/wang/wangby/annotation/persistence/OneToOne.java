package wang.wangby.annotation.persistence;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(OneToOnes.class)
public @interface OneToOne {

    //当前对象字段,默认是当前表主键
    String fk() default "";

    //关联的目标对象字段,默认和fk一样
    String references() default "";

    //关联的目标实体类
    @AliasFor("value")
    Class target() default Object.class;

    // 字段名称
    String fieldName() default "";
}