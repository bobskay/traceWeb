package wang.wangby.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import wang.wangby.annotation.monitor.MonitorHit;
import wang.wangby.config.Beans;
import wang.wangby.tools.monitor.HitMonitor;
import wang.wangby.tools.thread.ThreadPool;

import java.lang.reflect.Method;

@Aspect
@Slf4j
public class HitMonitorAspect {

    @Autowired
    Beans beans;
    ThreadPool threadPool;

    public HitMonitorAspect(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }


//    // 所有标注了SendMessage的方法
//    @Pointcut("@annotation(wang.wangby.annotation.monitor.MonitorHit)")
//    private void methods() {
//    }

    @Pointcut("@annotation(wang.wangby.annotation.monitor.MonitorHit)")
    private void methods() {
    }



    @Around("methods()")
    public Object process(ProceedingJoinPoint pjp) throws Throwable {
        Object o = pjp.proceed();
        threadPool.run(()->{
            addMonitor(pjp, o);
        });
        return pjp.proceed();

    }

    private void addMonitor(ProceedingJoinPoint pjp, Object o) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        HitMonitor monitor =beans.getHitMonitor(method.getDeclaringClass());
        if (monitor == null) {
            throw new RuntimeException("无法找到" + method + "对应的monitor,请确认该类实现了Monitor接口");
        }
        MonitorHit monitorHit = AnnotationUtils.getAnnotation(method, MonitorHit.class);
        int keyIdx = monitorHit.value();
        Object key = pjp.getArgs()[keyIdx];
        if (o == null) {
            monitor.miss(key);
        } else {
            monitor.hit(key);
        }
    }
}