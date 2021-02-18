package wang.wangby.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

//获取代码位置
public class CodeConvert extends ClassicConverter {  
  
    @Override  
    public String convert(ILoggingEvent event) {
    	StackTraceElement[] trace=event.getCallerData();
    	if(trace.length<1) {
    		return "";
    	}
    	int pos= trace[0].getClassName().lastIndexOf('.');
    	String className=trace[0].getClassName().substring(pos+1);
		//如果不是debug和info,就打印代码位置
		if(event.getLevel()==Level.DEBUG||event.getLevel()==Level.INFO) {
			return className+"."+trace[0].getMethodName()+":"+trace[0].getLineNumber() ;
		}
    	return className+"."+trace[0].getMethodName()+"("+ trace[0].getFileName()+":"+trace[0].getLineNumber()+")";
    }  
}
