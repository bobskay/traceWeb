package wang.wangby.annotation.monitor;

import java.lang.reflect.Method;

public interface MonitorNameCreateor {

     String getName(Method method, Object[] args);
}
