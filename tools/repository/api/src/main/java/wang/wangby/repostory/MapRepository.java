package wang.wangby.repostory;

import wang.wangby.base.entity.Entity;
import wang.wangby.utils.CacheUtil;
import wang.wangby.utils.Integers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MapRepository extends AbstractRepository {
    private Map<Class, Map<Long, Entity>> dataMap = new ConcurrentHashMap<>();

    @Override
    public <T extends Entity> T insert(T entity) {
        super.setId(entity);
        Map<Long, Entity> map = map(entity.getClass());
        Entity db=map.putIfAbsent(entity.id(), entity);
        if(db!=null){
            throw new RuntimeException("数据已经存在:"+map.get(entity.id()));
        }
        return entity;
    }

    private Map<Long, Entity> map(Class clazz) {
        return CacheUtil.get(dataMap, clazz, () -> new ConcurrentHashMap<>(), this);
    }


    @Override
    public <T extends Entity> List<T> select(T entity, int offset, int limit) {
        List<T> list = new ArrayList();
        Integers i = new Integers(-1);
        this.iterator(entity.getClass(), en -> {
            i.incrementAndGet();
            if(i.get()<offset){
                return true;
            }
            if(i.get()>offset+limit-1){
                return false;
            }
            list.add((T)en);
            return true;
        });
        return list;
    }

    @Override
    public int delete(Class<? extends Entity> clazz, Long id) {
        Entity en=map(clazz).remove(id);
        if(en==null){
            return 0;
        }
        return 1;
    }

    @Override
    public <T extends Entity> int update(T entity) {
        Entity t=map(entity.getClass()).get(entity.id());
        if(t==null){
            return 0;
        }
        map(entity.getClass()).put(entity.id(),entity);
        return 1;
    }

    @Override
    public <T extends Entity> T get(Class<T> clazz, Long id) {
        return (T) map(clazz).get(id);
    }

    @Override
    public boolean iterator(Class clazz, Function<Entity, Boolean> visitor) {
        for(Entity en:map(clazz).values()){
            if(!visitor.apply(en)){
                return false;
            }
        }
        return true;
    }

}
