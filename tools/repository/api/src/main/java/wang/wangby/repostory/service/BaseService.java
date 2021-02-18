package wang.wangby.repostory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import wang.wangby.Constants;
import wang.wangby.base.entity.Entity;
import wang.wangby.entity.Pagination;
import wang.wangby.function.Setter;
import wang.wangby.repostory.Repository;
import wang.wangby.repostory.dao.EntityDao;
import wang.wangby.repostory.selector.QueryUtil;
import wang.wangby.utils.ClassUtil;
import wang.wangby.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class BaseService<T extends Entity> {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private Class<? extends Entity> entityClass;

    @Autowired
    protected Repository repository;

    @Autowired
    protected QueryUtil queryUtil;

    public BaseService() {
        entityClass = ClassUtil.getFirstGenericType(this.getClass().getGenericSuperclass());
        if(entityClass==null){
            throw new RuntimeException("找不到当前service对应的entity:"+this.getClass());
        }
    }

    public int updateField(String fieldName, Object newValue, Object oldValue, long id) {
        return updateField(fieldName, newValue, oldValue, CollectionUtil.singleList(id));
    }

    //更新单个字段
    public int updateField(String fieldName, Object newValue, Object oldValue, List<Long> ids) {
        Map map = new HashMap<>();
        map.put("fieldName", fieldName);
        map.put("newValue", newValue);
        map.put("oldValues", CollectionUtil.singleList(oldValue));
        map.put("ids", ids);
        return this.defaultDao().updateField(map);
    }

    //setId,设置id的方法
    public Integer insert(T t, Setter<T, Long> setId) {
        setId.set(t, repository.newId());
        return defaultDao().insert(t);
    }


    public T get(Long id) {
        if (id == null) {
            throw new RuntimeException("通过主键查询时传入的ID为null");
        }
        return (T) defaultDao().get(id);
    }

    public List<T> get(List<Long> ids) {
        return defaultDao().getById(ids);
    }

    public int delete(Long[] id) {
        return defaultDao().deleteById(id);
    }

    //获取全部
    public List<T> getAll() {
        T en= ClassUtil.newInstance(entityClass);
        return repository.select(en,0, Constants.MAX_PER_QUERY);
    }

    public T unique(T t) {
        List list = repository.select(t, 0, 1);
        if (list.size() == 0) {
            return null;
        }
        return (T) list.get(0);
    }

    public int update(T t) {
        return defaultDao().updateById(t);
    }

    public EntityDao defaultDao() {
        return repository.getDao(entityClass);
    }

    public void insert(T t) {
        repository.insert(t);
    }

    public List<T> select(T query, Integer offset, Integer limit) {
        return repository.select(query, offset, limit);
    }

    public Pagination selectPage(T query, Integer offset, Integer limit) {
        EntityDao baseDao = this.defaultDao();
        if (offset == null) {
            offset = 0;
        }
        if (limit == null) {
            limit = Pagination.DEFAULT_SIZE;
        }
        long count = baseDao.getCount(query);
        if (offset > count) {
            return new Pagination(count, new ArrayList(), offset, limit);
        }
        List<T> list = repository.select(query,offset,limit);
        return new Pagination(count, list, offset, limit);
    }


    public void updateById(T t) {
        defaultDao().updateById(t);
    }

    public Integer deleteById(Long[] ids) {
        return defaultDao().deleteById(ids);
    }

    public Integer insertBatch(List<T> list) {
        return defaultDao().insertBatch(list);
    }

    public Long newId() {
        return repository.newId();
    }

}
