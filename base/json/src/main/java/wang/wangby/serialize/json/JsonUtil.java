package wang.wangby.serialize.json;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
abstract public class JsonUtil {
    public static JsonUtil INSTANCE=new FastJsonImpl();

    //将对象转为json
    abstract public String toString(Object obj);

    //将json转为java对象
    abstract public <T> T toBean(String str, Class<T> t);

    //将对象转为json并格式化
    abstract public String toFormatString(Object obj);

    //自定义的类型,因为反序列化的时候需要知道类名,所以将数据转为{className:{}}的型是
    public String customJs(Object obj) {
        Map map = new HashMap();
        map.put(obj.getClass().getName(), obj);
        return toString(map);
    }
    public String toString(String className, Object obj) {
        Map map = new HashMap();
        map.put(className, obj);
        return toString(map);
    }


    //js的格式为,所以直接直接从里面提取出className
    public Object parseCustomerJs(String formatedJs) {
        Map map = toBean(formatedJs, HashMap.class);
        String name = map.keySet().iterator().next() + "";
        try {
            Class clazz = Class.forName(name);
            return toBean(map.values().iterator().next() + "", clazz);
        } catch (Exception ex) {
            throw new RuntimeException("无法自动转为java对象" + formatedJs+":"+ex.getMessage());
        }
    }

}
