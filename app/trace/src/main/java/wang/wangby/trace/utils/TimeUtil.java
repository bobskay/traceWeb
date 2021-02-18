package wang.wangby.trace.utils;

import wang.wangby.utils.DateTime;

import java.util.Date;

public class TimeUtil {

    public static String showTime(Date start, Date end) {
        if (end == null) {
            end = new Date();
        }
        long time = end.getTime() - start.getTime();
        if (time > 1000) {
            time = time / 1000 * 1000;
        }
        if (time < 1000) {
            return time + "ms";
        }
        StringBuilder sb = new StringBuilder();

        long hour = time / 60 / 60 / 1000;
        sb.append(hour + ":");

        time = time % (60 * 60 * 1000);
        long min = time / 60 / 1000;
        if(min<10){
            sb.append("0"+min + ":");
        }else{
            sb.append(min + ":");
        }


        time = time % (60 * 1000);
        long second = time / 1000;
        if(second<10){
            sb.append("0"+second );
        }else{
            sb.append(second);
        }
        return sb.toString();
    }

    public static String today() {
        return DateTime.current().toString(DateTime.Format.YEAR_TO_DAY);
    }

}
