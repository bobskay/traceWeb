package wang.wangby.template.velocity;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.util.introspection.VelPropertyGet;
import wang.wangby.template.SimpleObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 为原始类型添加自定义的格式转换方法
 *
 * */
@Slf4j
public class PrimitiveTypePropertyGet implements VelPropertyGet {
    private static Map<String, Function<SimpleObject,Object>> getterMap=new HashMap<>();
    static{
        getterMap.put("date", SimpleObject::getDate);
        getterMap.put("dateTime", SimpleObject::getDateTime);
        getterMap.put("json", SimpleObject::getJson);
        getterMap.put("yesOrNo", SimpleObject::getYesOrNo);
    }
    String identifier;
    public PrimitiveTypePropertyGet(String identifier){
        this.identifier=identifier;
    }
    
    @Override
    public Object invoke(Object o) throws Exception {
        Function<SimpleObject,Object> fun=getterMap.get(identifier);
        if(fun!=null){
            return fun.apply(new SimpleObject(o));
        }
        log.warn("未实现的方法:"+identifier);
        return null;
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    @Override
    public String getMethodName() {
        return null;
    }
}
