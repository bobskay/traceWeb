package wang.wangby.test.beanfactory;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//测试用beanfactory,因为所有bean注入的时候都是Autowired,所以如果发现某个属性没有就直接new
//通过此工厂创建的bean必须都是单例的
//对于没有空构造方法的类必须在get之前手动put一个进来
public class TestBeanFactory {
    private static Map<Class, Object> beanMap = testMap();

    public static ConcurrentHashMap<Class, Object> testMap() {
        ConcurrentHashMap map = new ConcurrentHashMap<>();
        return map;
    }

    public static void put(Class clazz, Object bean) {
        beanMap.put(clazz, bean);
    }

    public static <T> T get(Class<T> clazz) {
        return AutowiredClassLazyLoader.get(clazz, beanMap);
    }
}
