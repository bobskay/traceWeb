package wang.wangby.test.beanfactory;

import org.mockito.cglib.proxy.Enhancer;
import org.mockito.cglib.proxy.LazyLoader;
import org.springframework.beans.factory.annotation.Autowired;
import wang.wangby.utils.ClassUtil;

import java.lang.reflect.Field;
import java.util.Map;


public class AutowiredClassLazyLoader implements LazyLoader {
    private Class target;
    private Map<Class, Object> existedBeans;

    public AutowiredClassLazyLoader(Class target, Map<Class, Object> existedBeans) {
        this.existedBeans = existedBeans;
        this.target = target;
    }

    public Object loadObject() throws Exception {
        Object bean = existedBeans.get(target);
        if (bean != null) {
            return bean;
        }
        bean = ClassUtil.newInstance(target);
        //所有标记了Autowired的字段也设置为懒加载
        for (Field f : ClassUtil.getAllFields(target)) {
            Autowired autowired = f.getAnnotation(Autowired.class);
            if (autowired == null) {
                continue;
            }
            f.setAccessible(true);
            f.set(bean, get(f.getType(), existedBeans));
        }
        return bean;
    }

    public static <T> T get(Class<T> clazz, Map beanMap) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        AutowiredClassLazyLoader loader = new AutowiredClassLazyLoader(clazz, beanMap);
        return (T) enhancer.create(clazz, loader);
    }
}
