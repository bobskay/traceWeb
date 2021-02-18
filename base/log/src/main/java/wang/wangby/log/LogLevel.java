package wang.wangby.log;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//设置日志级别
public class LogLevel {
    public static void trace(Class clazz){
        changeLevel(clazz,Level.TRACE);

    }
    public static void debug(Class clazz){
        changeLevel(clazz,Level.DEBUG);
    }

    public static void info(Class clazz){
        changeLevel(clazz,Level.INFO);
    }

    public static void changeLevel(Class clazz,Level level){
        try{
            Logger logger=LoggerFactory.getLogger(clazz);
            ch.qos.logback.classic.Logger log=(ch.qos.logback.classic.Logger)logger;
            log.setLevel(level);
        }catch (Exception ex){
            System.out.println("修改日志级别出错,要修改的类:"+clazz);
            ex.printStackTrace();
        }
    }
}
