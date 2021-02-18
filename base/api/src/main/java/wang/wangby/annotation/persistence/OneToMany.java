package wang.wangby.annotation.persistence;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(OneToManys.class)
public @interface OneToMany {

    //当前对象字段,默认是当前表主键
    String fk() default "";

    //关联的目标对象字段,默认和fk一样
    String references() default "";

    String fieldName() default "";

    //关联的目标实体类
    Class target() default Object.class;
}
