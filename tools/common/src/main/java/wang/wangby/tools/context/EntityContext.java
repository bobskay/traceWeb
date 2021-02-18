package wang.wangby.tools.context;

import java.util.HashMap;
import java.util.Map;

public class EntityContext {
    private static final ThreadLocal<Map<Object, Map>> ENTITY_MAP = new ThreadLocal<Map<Object, Map>>(){
        public Map<Object, Map> initialValue(){
            return new HashMap();
        }
    };

    //实体的额为字段
    public static <T> Map<String,T> getExt(Object entity) {
        Map map = ENTITY_MAP.get().get(entity);
        if (map == null) {
            map = new HashMap();
            ENTITY_MAP.get().put(entity, map);
        }
        return map;
    }
}
