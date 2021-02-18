package wang.wangby.trace.utils;

import wang.wangby.utils.DateTime;

import java.util.Date;

public class TimeUtil {

    public static String showTime(Date start, Date end){
        if(end==null){
            end=new Date();
        }
        long time=end.getTime()-start.getTime();
        if(time>1000){
            time=time/1000*1000;
        }
        return DateTime.showTime(time);
    }
}
