package wang.wangby.repository;

import org.springframework.beans.factory.annotation.Autowired;
import wang.wangby.base.entity.Entity;
import wang.wangby.repostory.AbstractRepository;
import wang.wangby.repostory.EntityDaoFinder;
import wang.wangby.repostory.dao.EntityDao;
import wang.wangby.repostory.selector.QueryUtil;
import wang.wangby.utils.ClassUtil;
import wang.wangby.utils.IdWorker;

import java.util.List;
import java.util.function.Function;

public class MysqlRepository extends AbstractRepository {

    @Autowired
    EntityDaoFinder finder;
    @Autowired
    private QueryUtil queryUtil;
    @Autowired
    IdWorker idWorker;

    @Override
    public Entity init(Entity entity, String fieldNames) {
        queryUtil.fill(entity, fieldNames);
        return entity;
    }

    @Override
    public <T extends Entity> T insert(T entity) {
        Long id = entity.id();
        if (id == null) {
            entity.id(idWorker.nextId());
        }
        EntityDao dao = finder.getDao(entity.getClass());
        dao.insert(entity);
        return entity;
    }

    @Override
    public <T extends Entity> List<T> select(T entity, int offset, int limit) {
        entity.set(QueryUtil.OFFSET, offset);
        entity.set(QueryUtil.LIMIT, limit);
        return getDao(entity.getClass()).select(entity);
    }


    @Override
    public int delete(Class<? extends Entity> clazz, Long id) throws Exception {
        return finder.getDao(clazz).delete(id);
    }

    @Override
    public <T extends Entity> int update(T entity) throws Exception {
        return getDao(entity.getClass()).updateById(entity);
    }


    @Override
    public <T extends Entity> T get(Class<T> clazz, Long id) throws Exception {
        return (T)getDao(clazz).get(id);
    }

    @Override
    public boolean iterator(Class clazz, Function<Entity, Boolean> visitor) {
        Entity query = ClassUtil.newInstance(clazz);
        int offset = 0;
        while (true) {
            List<Entity> list = this.select(query, offset, 10000);
            if (list.size() == 0) {
                return true;
            }
            for (Entity e : list) {
                offset++;
                if (!visitor.apply(e)) {
                    return false;
                }
            }
        }
    }

    @Override
    public EntityDao getDao(Class<? extends Entity> entityClass) {
        return finder.getDao(entityClass);
    }


}
