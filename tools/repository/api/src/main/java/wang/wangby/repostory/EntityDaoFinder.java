package wang.wangby.repostory;

import wang.wangby.repostory.dao.EntityDao;

import java.util.function.Function;

public class EntityDaoFinder {
    private Function<Class, EntityDao> entityDaoFinder;

    public EntityDaoFinder(){}

    public EntityDaoFinder(Function<Class, EntityDao> entityDaoFinder){
        this.entityDaoFinder=entityDaoFinder;
    }

    public EntityDao getDao(Class clazz){
        return entityDaoFinder.apply(clazz);
    }
}
