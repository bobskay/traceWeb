package wang.wangby.annotation.monitor;

import java.lang.reflect.Method;

public class DefaultMonitorName implements MonitorNameCreateor {
    @Override
    public String getName(Method method, Object[] args) {
        return method.getDeclaringClass().getName()+"."+method.getName();
    }
}
