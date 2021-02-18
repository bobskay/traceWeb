package wang.wangby.repostory;

import wang.wangby.base.entity.Entity;
import wang.wangby.repostory.dao.EntityDao;

import java.util.List;
import java.util.function.Function;

public interface Repository {

    <T extends Entity> T insert(T entity) ;

    <T extends Entity> T init(T entity, String fieldNames);

    // 查询列表
    <T extends Entity> List<T> select(T entity, int offset, int limit);

    Long newId();

    // 通过主键删除记录
    int delete(Class<? extends Entity> clazz, Long id) throws Exception;

    //更新数据
    <T extends Entity> int update(T model) throws Exception;


    <T extends Entity> T get(Class<T> clazz, Long id) throws Exception;

    //@return 是否遍历了所有数据
    boolean iterator(Class clazz, Function<Entity, Boolean> visitor) throws  Exception;

    EntityDao getDao(Class<? extends Entity> entityClass);
}
