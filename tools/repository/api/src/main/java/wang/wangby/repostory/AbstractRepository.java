package wang.wangby.repostory;

import org.springframework.beans.factory.annotation.Autowired;
import wang.wangby.base.entity.Entity;
import wang.wangby.repostory.dao.EntityDao;
import wang.wangby.utils.IdWorker;

abstract  public class AbstractRepository implements Repository {

    @Autowired
    IdWorker idWorker;

    public Long newId(){
        return idWorker.nextId();
    }

    //如果主键为空就新增主键
    public void setId(Entity entity) {
        if(entity.id()==null){
            entity.id(newId());
        }
    }

    @Override
    public <T extends Entity> T init(T entity, String fieldNames) {
        throw new RuntimeException("未实现");
    }

    @Override
    public EntityDao getDao(Class<? extends Entity> entityClass) {
        throw new RuntimeException("未实现");
    }
}
