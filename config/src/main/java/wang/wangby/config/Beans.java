package wang.wangby.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import wang.wangby.annotation.monitor.MonitorHit;
import wang.wangby.repostory.dao.EntityDao;
import wang.wangby.tools.monitor.HitMonitor;
import wang.wangby.tools.monitor.MonitorData;
import wang.wangby.tools.monitor.TimeMonitor;
import wang.wangby.tools.thread.ThreadPool;
import wang.wangby.tools.thread.ThreadPoolConfig;
import wang.wangby.tools.thread.job.Job;
import wang.wangby.tools.thread.job.JobConfig;
import wang.wangby.tools.thread.job.JobInfo;
import wang.wangby.tools.thread.job.ScheduledExecutor;
import wang.wangby.utils.ClassUtil;

import javax.swing.text.html.parser.Entity;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class Beans implements InitializingBean {
    //统计所有方法上标记里MonitorHit的数据
    @Getter
    private Map<Class, HitMonitor> hitMonitorMap = new ConcurrentHashMap<>();
    private Map<String, TimeMonitor> timeMonitorMap = new ConcurrentHashMap<>();
    private Map<String, ThreadPool> threadPoolMap = new ConcurrentHashMap<>();
    private Map<String, ScheduledExecutor> scheduledMap = new ConcurrentHashMap<>();
    private Map<Class<? extends Entity>, EntityDao> entityDaoMap= new ConcurrentHashMap<>();

    @Autowired
    ApplicationContext applicationContext;

    public <T> Map<String, T> getBeansOfType(Class clazz) {
        return applicationContext.getBeansOfType(clazz);
    }

    public HitMonitor getHitMonitor(Class<?> declaringClass) {
        return hitMonitorMap.get(declaringClass);
    }

    public void iteratorMonitorData(Consumer<MonitorData> monitorConsumer) {
        hitMonitorMap.values().forEach(monitorConsumer);
        threadPoolMap.values().forEach(monitorConsumer);
        timeMonitorMap.values().forEach(monitorConsumer);
        applicationContext.getBeansOfType(MonitorData.class).values().forEach(monitorConsumer);
    }

    public ThreadPool newThreadPool(String name ,int core) {
        return newThreadPool(ThreadPoolConfig.newInstance(name,core));
    }

    public ThreadPool newThreadPool(ThreadPoolConfig config) {
        ThreadPool threadPool = threadPoolMap.get(config.getName());
        if (threadPool != null) {
            log.error("重复的线程池"+config.getName()+":"+threadPool.getCreator());
            throw new RuntimeException("线程池已经存在,请检查代码:" + config.getName());
        }
        log.info("创建线程池:"+config);
        threadPool = new ThreadPool(config.getName(), config.getCore(), config.getMax(), config.getQueueSize(), config.getKeepAliveSecond());
        threadPoolMap.put(config.getName(), threadPool);
        return threadPool;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initHitMonitor();
        initEntityDao();
        initJob();
    }

    private void initJob() {
        Collection<Job> jobs= applicationContext.getBeansOfType(Job.class).values();
        for(Job jb:jobs){
            JobConfig jobConfig=jb.jobConfig();
            if(jobConfig!=null){
                newSchedule(jobConfig);
            }
        }
    }

    public JobInfo newSchedule(JobConfig config) {
        ScheduledExecutor executor = scheduledMap.get(config.getName());
        if (executor != null) {
            log.error("计划任务已经存在"+config.getName()+":"+executor.getJobInfo().getCreateInfo());
            throw new RuntimeException("计划任务已经存在,请检查代码:" + config.getName());
        }
        log.info("创建计划任务:"+config);
        executor = new ScheduledExecutor(config);
        scheduledMap.put(config.getName(), executor);
        return executor.getJobInfo();
    }

    private void initHitMonitor() {
        applicationContext.getBeansWithAnnotation(MonitorHit.class).forEach((id, monitor) -> {
            log.info("新增监控:"+id);
            Class clazz = ClassUtil.getTargetClass(monitor);
            hitMonitorMap.put(clazz, new HitMonitor(id));
        });
    }


    public void initEntityDao() {
        Collection<EntityDao> entityDaos = applicationContext.getBeansOfType(EntityDao.class).values();
        log.info("初始化dao："+entityDaos);
        entityDaos.forEach(entityDao -> {
            Class clazz = getModel(entityDao.getClass());
            if (clazz == null) {
                log.debug("跳过mapper:" + entityDao);
                return;
            }
            entityDaoMap.put(clazz, entityDao);
        });
    }

    public static Class getModel(Class daoClass) {
        if (daoClass == Object.class) {
            return null;
        }
        for (Type type : daoClass.getGenericInterfaces()) {
            if (type instanceof Class) {
                return getModel((Class)type);
            } else {
                return ClassUtil.getFirstGenericType(type);
            }
        }
        return null;
    }

    public EntityDao getEntityDao(Class<? extends Entity> clazz) {
        return entityDaoMap.get(clazz);
    }

    public TimeMonitor newTimeMonitor(String name) {
        TimeMonitor m=timeMonitorMap.get(name);
        if(m!=null){
            return m;
        }
        ThreadPool pool=newThreadPool(ThreadPoolConfig.newInstance("timeMonitor_"+name,10));
        TimeMonitor timeMonitor=new TimeMonitor(name,TimeMonitor.DEFLUT_ACCURACY,pool);
        timeMonitorMap.put(name,timeMonitor);
        return timeMonitor;
    }

    public void iteratorScheduledMap(Consumer<ScheduledExecutor> consumer){
        scheduledMap.values().forEach(consumer);
    }

    public void shutdownSchedule(String id) {
        ScheduledExecutor scheduledExecutor=scheduledMap.get(id);
        scheduledExecutor.shutdown();
        scheduledMap.remove(id);
    }


    public Set<Class<? extends Entity>> getEntities() {
        return entityDaoMap.keySet();
    }
}
