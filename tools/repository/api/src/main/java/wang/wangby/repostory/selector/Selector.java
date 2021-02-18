package wang.wangby.repostory.selector;

import wang.wangby.base.entity.Entity;
import wang.wangby.repostory.dao.EntityDao;

import java.util.List;

public class Selector {

    private EntityDao entityDao;
    private Entity query;

    public Selector(EntityDao entityDao, Entity query){
        this.entityDao=entityDao;
        this.query=query;
    }

    public Condition attr(String name){
        Condition condition=new Condition(this,query,name);
        return condition;
    }


    public List list(){
        return entityDao.select(query);
    }

    public <T> T get(){
        List<T> list=this.list();
        if(list.size()==0){
            return null;
        }
        return list.get(0);
    }

    public int delete() {
        return entityDao.delete(query.id());
    }

    public long count() {
        return entityDao.getCount(query);
    }
}
