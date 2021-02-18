package wang.wangby.log;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.wangby.Constants;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtil {
	private static Logger log=LoggerFactory.getLogger(LogUtil.class);

	/** 获取原始异常 */
	public static Throwable getCause(Throwable ex){
		while(true){
			if(ex.getCause()==null){
				return ex;
			}else{
				ex=ex.getCause();
			}
		}
	}

	/**
	 * 高亮显示打印信息，在编辑器里可以直接跳转
	 * 
	 */
	public static void debug(Object msg){
		RuntimeException ex=new RuntimeException(msg+"");
		StringWriter writer=new StringWriter();
		ex.printStackTrace(new PrintWriter(writer));
		String text=writer.toString();
		int first=text.indexOf(Constants.LINE);
		int second=text.indexOf(Constants.LINE,first+1);
		int third=text.indexOf(Constants.LINE,second+1);
		text=text.substring(0,first)+text.substring(second,third);
		log.error(Constants.LINE+text);
	}

	//打印调用者信息
	public static void debugCaller(Exception ex){
		StackTraceElement[] st=ex.getStackTrace();
		StackTraceElement m=st[1];
		if(st.length>2) {
			m=st[2];
		}
		String className=m.getClassName();
		int pos=className.lastIndexOf('.');
		className=className.substring(pos);
		log.debug(""+ex.getMessage()+" at "+className+"."+m.getMethodName()+"("+m.getFileName()+":"+m.getLineNumber()+")");
	}

	//打印调用者
	public static void debugCaller(Object msg){
		RuntimeException ex=new RuntimeException(msg+"");
		StackTraceElement[] st=ex.getStackTrace();
		StackTraceElement m=st[1];
		if(st.length>2) {
			m=st[2];
		}
		String className=m.getClassName();
		int pos=className.lastIndexOf('.');
		className=className.substring(pos);
		log.debug(""+msg+" at "+className+"."+m.getMethodName()+"("+m.getFileName()+":"+m.getLineNumber()+")");
	}

	public static void changeLevel(Class clazz, Level level){
		LogLevel.changeLevel(clazz,level);
	}

	//获得调用者信息
	public static String getCalller(){
		RuntimeException ex=new RuntimeException();
		StackTraceElement[] st=ex.getStackTrace();
		StackTraceElement m=st[1];
		if(st.length>2) {
			m=st[2];
		}
		String className=m.getClassName();
		int pos=className.lastIndexOf('.');
		className=className.substring(pos+1);
		return className+"."+m.getMethodName()+"("+m.getFileName()+":"+m.getLineNumber()+")";
	}

	public static void tt() {
		debugCaller("sss");
	}
	
	/** 将异常转为文本 */
	public static String getExceptionText(Throwable throwable){
		if(throwable==null){
			return "";
		}
		StringWriter writer=new StringWriter();
		throwable.printStackTrace(new PrintWriter(writer));
		String text=writer.toString();
		return text;
	}
}
