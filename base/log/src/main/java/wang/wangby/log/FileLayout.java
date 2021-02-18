package wang.wangby.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.LayoutBase;
import org.slf4j.MDC;

//输出到文件的格式
public class FileLayout extends LayoutBase<ILoggingEvent> {
    public static final String SPLIT = "\t";
    public static final String REQUEST_ID = "requestId";

    @Override
    public String doLayout(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.getTimeStamp() + SPLIT);
        sb.append(event.getLevel() + SPLIT);
        sb.append(event.getThreadName() + SPLIT);
        String requestId = MDC.get(REQUEST_ID);
        if (requestId == null || "".equals(requestId)) {
            requestId = "-";
        }
        sb.append(requestId + SPLIT);
        StackTraceElement[] trace = event.getCallerData();
        if (trace.length < 1) {
            sb.append("-" + SPLIT + "-" + SPLIT + "-" + SPLIT);
        }
        int pos = trace[0].getClassName().lastIndexOf('.');
        String className = trace[0].getClassName().substring(pos + 1);
        sb.append(className + SPLIT);
        sb.append(trace[0].getMethodName() + SPLIT);
        sb.append(trace[0].getLineNumber() + SPLIT);

        String message = event.getMessage().replace("\n", "\\n").replace("\t", "\\t");
        sb.append(message);

        //异常信息
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (throwableProxy != null) {
            sb.append("\\n" + throwableProxy.getClassName() + ":" + throwableProxy.getMessage() + "\\n");
            for (StackTraceElementProxy stack : throwableProxy.getStackTraceElementProxyArray()) {
                sb.append("\\t");
                sb.append(stack.getStackTraceElement().getClassName() + " ");
                sb.append(stack.getStackTraceElement().getMethodName() + " ");
                sb.append(stack.getStackTraceElement().getLineNumber() + " ");
                sb.append("\\n");
            }
        }
        return sb.toString() + "\n";
    }
}
