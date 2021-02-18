package wang.wangby.repostory.dao;

import java.util.List;
import java.util.Map;

public interface EntityDao<T> {

    // 新增记录
    int insert(T entity);

    // 通过主键删除记录
    int deleteById(Long[] id);

    // 通过主键删除记录
    int delete(Long id);

    // 通过主键更新记录,如果要将字段更新为null,用updateFull
    int updateById(T entity);

    /**
     * 根据条件更新单个字段
     *
     * @param map.field     要更新的字段名称
     * @param map.ids       要更新数据的主键,list
     * @param map.newValue  新值
     * @param map.oldValues 旧值,list
     */
    int updateField(Map map);

    // 通过主键更新记录,所有字段都更新
    int updateFull(T entity);

    /**
     * 查询列表,如果entity.ext.offset ,entity.ext.limit 有值,就分页
     *
     * @param entity.ext.offset     起始位置
     * @param entity.ext.limit      返回条数
     */
    List<T> select(T entity);

    // 获得记录条数
    long getCount(T entity);

    // 通过主键获得记录
    T get(long id);

    // 通过主键获得记录
    List<T> getById(List<Long> id);

    //批量插入
    int insertBatch(List<T> list);


}
