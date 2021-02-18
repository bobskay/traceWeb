package wang.wangby.persistence.file;

import lombok.extern.slf4j.Slf4j;
import wang.wangby.base.entity.Entity;
import wang.wangby.repostory.Repository;
import wang.wangby.repostory.dao.EntityDao;
import wang.wangby.tools.thread.job.Job;
import wang.wangby.tools.thread.job.JobConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public class AsynRepository  implements Repository, Job {

    private BlockingQueue<PersistenceAction> actions;//未持久化的动作
    private Map<Class, Map<Long, Entity>> data = new ConcurrentHashMap<>();//所有数据
    private int retry = 2;
    private int periodSecond;


    Repository repository;

    /**
     * * @param repository   实际的持久化类
     * * @param maxSize      最多允许保存多少条未删数据
     * * @param periodSecond 刷盘间隔
     */
    public AsynRepository(Repository repository, Set<Class> entityClass, int maxSize, int periodSecond) {
        this.repository = repository;
        data = new ConcurrentHashMap<>();
        this.periodSecond=periodSecond;
        actions = new ArrayBlockingQueue(maxSize);
        entityClass.forEach(clz -> {
            Map map = new ConcurrentHashMap();
            try {
                repository.iterator(clz, en -> {
                    map.put(en.id(), en);
                    return true;
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            data.put(clz, map);
        });
    }

    //保存数据到磁盘
    public void flush() {
        while (true) {
            PersistenceAction action = actions.poll();
            if (action == null) {
                return;
            }
            try {
                executeAction(action, 1);
            } catch (Exception e) {
                log.error("持久化操作出错", e);
                return;
            }
        }
    }

    private void executeAction(PersistenceAction action, int no) throws InterruptedException {
        if (no > retry) {
            throw new RuntimeException("执行" + no + "次都出错,终止本次持久化动作");
        }
        try {
            action.execute();
        } catch (Exception e) {
            log.error("第{}此执行持久化动作出错", no, e);
            long sleep = (long) (Math.pow(no, 2) * 1000);
            Thread.sleep(sleep);
            executeAction(action, no + 1);
        }
    }

    @Override
    public Entity insert(Entity entity) {
        Long id = entity.id();
        if (id == null) {
           entity.id(newId());
        }
        Entity db = data.get(entity.getClass()).putIfAbsent(entity.id(), entity);
        if (db != null) {
            throw new RuntimeException("数据已经存在,model=" + db);
        }
        actions.add(() -> repository.insert(entity));
        return entity;
    }


    @Override
    public int update(Entity entity) {
        data.get(entity.getClass()).put(entity.id(), entity);
        actions.add(() -> {
            repository.update(entity);
        });
        return 1;
    }

    @Override
    public <T extends Entity> T init(T entity, String fieldNames) {
        return repository.init(entity, fieldNames);
    }

    @Override
    public <T extends Entity> List<T> select(T entity, int offset, int limit) {
        List list = new ArrayList();
        iterator(entity.getClass(), e -> {

            return true;
        });
        return list;
    }

    @Override
    public Long newId() {
        return repository.newId();
    }

    @Override
    public int delete(Class<? extends Entity> clazz, Long id) throws Exception {
        data.remove(id);
        actions.add(() -> {
            repository.delete(clazz, id);
        });
        return 1;
    }

    @Override
    public <T extends Entity> T get(Class<T> clazz, Long id) throws Exception {
        return (T) data.get(clazz).get(id);
    }

    @Override
    public boolean iterator(Class clazz, Function<Entity, Boolean> visitor) {
        for (Entity e : data.get(clazz).values()) {
            if (!visitor.apply(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public EntityDao getDao(Class<? extends Entity> entityClass) {
        return repository.getDao(entityClass);
    }

    @Override
    public JobConfig jobConfig() {
        JobConfig jobConfig=new JobConfig();
        jobConfig.setName("AsynRepositoryFlush");
        jobConfig.setDelay(periodSecond);
        jobConfig.setPeriod(periodSecond);
        jobConfig.setTimeUnit(TimeUnit.SECONDS);
        jobConfig.setRunnable(this::flush);
        return jobConfig;
    }
}
