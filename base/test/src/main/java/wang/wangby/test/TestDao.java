package wang.wangby.test;

import wang.wangby.repostory.dao.EntityDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDao implements EntityDao {
    private Map<Long,Object> map=new HashMap<>();

    public void put(Long id,Object obj){
        map.put(id,obj);
    }

    @Override
    public int insert(Object entity) {
        return 0;
    }

    @Override
    public int deleteById(Long[] id) {
        return 0;
    }

    @Override
    public int delete(Long id) {
        return 0;
    }

    @Override
    public int updateById(Object entity) {
        return 0;
    }

    @Override
    public int updateField(Map map) {
        return 0;
    }

    @Override
    public int updateFull(Object entity) {
        return 0;
    }

    @Override
    public List select(Object entity) {
        return new ArrayList();
    }

    @Override
    public long getCount(Object entity) {
        return 0;
    }

    @Override
    public Object get(long id) {
        return map.get(id);
    }

    @Override
    public int insertBatch(List list) {
        return 0;
    }

    @Override
    public List getById(List ids) {
        List list=new ArrayList();
        for(Object i:ids){
            Object o=map.get(i);
            if(o!=null){
                list.add(o);
            }
        }
        return list;
    }
}
