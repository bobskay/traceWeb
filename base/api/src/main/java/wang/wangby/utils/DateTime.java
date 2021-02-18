package wang.wangby.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期对象,继承java.util.Date
 */
@Slf4j
public class DateTime extends Date{


	public enum Format{
		YEAR_TO_YEAR("yyyy"),
		YEAR_TO_MONTH("yyyy-MM"),
		YEAR_TO_DAY("yyyy-MM-dd"),
		YEAR_TO_HOUR("yyyy-MM-dd HH"),
		YEAR_TO_MINUTE("yyyy-MM-dd HH:mm"),
		YEAR_TO_SECOND("yyyy-MM-dd HH:mm:ss"),
		YEAR_TO_MILLISECOND("yyyy-MM-dd HH:mm:ss.SSS"),
		MONTH_TO_MONTH("MM"),
		MONTH_TO_DAY("MM-dd"),
		MONTH_TO_HOUR("MM-dd HH"),
		MONTH_TO_MINUTE("MM-dd HH:mm"),
		MONTH_TO_SECOND("MM-dd HH:mm:ss"),
		MONTH_TO_MILLISECOND("MM-dd HH:mm:ss.SSS"),
		DAY_TO_DAY("dd"),
		DAY_TO_HOUR("dd HH"),
		DAY_TO_MINUTE("dd HH:mm"),
		DAY_TO_SECOND("dd HH:mm:ss"),
		DAY_TO_MILLISECOND("dd HH:mm:ss.SSS"),
		HOUR_TO_HOUR("HH"),
		HOUR_TO_MINUTE("HH:mm"),
		HOUR_TO_SECOND("HH:mm:ss"),
		HOUR_TO_MILLISECOND("HH:mm:ss.SSS"),
		MINUTE_TO_MINUTE("mm:ss.SSS"),
		MINUTE_TO_SECOND("mm:ss"),
		MINUTE_TO_MILLISECOND("mm:ss.SSS"),
		SECOND_TO_SECOND("ss"),
		SECOND_TO_MILLISECOND("ss.SSS"),
		MILLISECOND_TO_MILLISECOND("SSS"),
		YEAR_TO_SECOND_STRING("yyyyMMddHHmmss"),
		YEAR_TO_MILLISECOND_STRING("yyyyMMddHHmmssSSS"),
		YEAR_TO_DAY_STRING("yyyyMMdd"),
		RFC3339("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		private String format;
		Format(String format){
			this.format=format;
		}
	}
	private Format format;
	//显示纳秒字符串
	public static String showNs(double time) {
		if(time<1000){
			return (long)time+"ns";
		}
		//不到1毫秒
		if(time<1000*1000){
			time=time/1000/1000;
			return StringUtil.round(time,4)+"ms";
		}
		//不到1秒
		if(time<1000*1000*1000){
			time=time/1000/1000;
			return StringUtil.round(time,2)+"ms";
		}
		time=time/1000/1000/1000;
		return StringUtil.round(time,2)+"s";
	}

	/**将一段纳秒时间输出为友好格式*/
	public static String showNs(Long begin, Long end) {
		if(begin==null||end==null){
			return "";
		}
		if(end<begin){
			return "-1";
		}
		return showNs(end-begin);
	}

	public boolean isLeap(){
		return isLeap(this.getYear());
	}

	/** 判断某年是否闰年 */
	public static boolean isLeap(int year){
		if(year%4==0&&year%100!=0){
			return true;
		}
		if(year%400==0){
			return true;
		}
		return false;
	}

	public DateTime(Date date){
		this(date.getTime());
	}

	public DateTime(long time){
		if(time==new DateTime(time, Format.YEAR_TO_DAY).getTime()){
			init(new Date(time), Format.YEAR_TO_DAY);
		}else{
			init(new Date(time), Format.YEAR_TO_SECOND);
		}
	}

	public DateTime(long time,Format type){
		this(new Date(time),type);
	}

	/**
	 * 通过默认格式创建时间
	 * 
	 * @param dateString
	 *        时间串,格式必须为yyyy-MM-dd HH:mm:SS或yyyy-mm-dd
	 */
	public DateTime(String dateString){
		if(dateString.indexOf(" ")==-1){
			init(dateString, Format.YEAR_TO_DAY);
		}else{
			init(dateString, Format.YEAR_TO_SECOND);
		}

	}

	/**
	 * 通过字符串创建时间对象
	 * 
	 * @param dateString
	 * @param type 日期格式,详见DateTime.getDateFormat
	 *        DateTime.YEAR_TO_YEAR yy
	 *        DateTime.YEAR_TO_MONTH yy-mm
	 *        DateTime.YEAR_TO_DAY yy-mm-dd
	 *        ....
	 */
	public DateTime(String dateString,Format type){
		init(dateString,type);
	}

	// 初始化当前对象,所有创建方法最终都得调到这里
	private void init(String dateTimeString,Format format){
		try{
			SimpleDateFormat dateFormat=getDateFormat(format);
			Date date=dateFormat.parse(dateTimeString);
			this.format=format;
			super.setTime(date.getTime());
		}catch(ParseException e){
			throw new IllegalArgumentException("unable to parse "+dateTimeString,e);
		}
	}

	public DateTime(Date date,Format format){
		SimpleDateFormat dateFormat=getDateFormat(format);
		this.init(dateFormat.format(date),format);
	}

	private void init(Date date,Format format){
		SimpleDateFormat dateFormat=getDateFormat(format);
		this.init(dateFormat.format(date),format);
	}

	public String toString(){
		return toString(format);
	}

	@SuppressWarnings("deprecation")
	public int getYear(){
		return Integer.parseInt(getDateFormat(Format.YEAR_TO_YEAR).format(this));
	}
	@SuppressWarnings("deprecation")
	public int getMonth(){
		return Integer.parseInt(getDateFormat(Format.MONTH_TO_MONTH).format(this));
	}
	@SuppressWarnings("deprecation")
	public int getDay(){
		return Integer.parseInt(getDateFormat(Format.DAY_TO_DAY).format(this));
	}

	// 周几
	public int getWeek(){
		Calendar aCalendar=Calendar.getInstance();
		aCalendar.setTime(this);
		int week=aCalendar.get(Calendar.DAY_OF_WEEK);
		if(week==1){
			return 7;
		}else{
			return week-1;
		}
	}

	// 当前月份的天数
	public int getDaysOfMonth(){
		Calendar time=Calendar.getInstance();
		time.clear();
		time.set(Calendar.YEAR,this.getYear());
		// year年
		time.set(Calendar.MONTH,this.getMonth()-1);
		// Calendar对象默认一月为0,month月
		int day=time.getActualMaximum(Calendar.DAY_OF_MONTH);
		return day;
	}

	public int getHour(){
		return Integer.parseInt(getDateFormat(Format.HOUR_TO_HOUR).format(this));
	}

	public int getMinute(){
		return Integer.parseInt(getDateFormat(Format.MINUTE_TO_MINUTE).format(this));
	}

	public int getSecond(){
		return Integer.parseInt(getDateFormat(Format.SECOND_TO_SECOND).format(this));
	}

	public static SimpleDateFormat getDateFormat(Format format){
		return getFormat(format.format);
	}

	public String toString(Format format){
		SimpleDateFormat dateFormat=getDateFormat(format);
		return dateFormat.format(this);
	}

	public String toString(String pattern){
		SimpleDateFormat dateFormat=getFormat(pattern);
		return dateFormat.format(this);
	}

	public static String toString(long time,Format format){
		SimpleDateFormat dateFormat=getDateFormat(format);
		return dateFormat.format(time);
	}

	public static String toString(Date time,Format format){
		SimpleDateFormat dateFormat=getDateFormat(format);
		return dateFormat.format(time);
	}

	public static DateTime current(){
		return new DateTime(new Date(), Format.YEAR_TO_MILLISECOND);
	}

	public static DateTime current(Format format){
		return new DateTime(new Date(),format);
	}

	public DateTime addDay(int day){
		DateTime dt=new DateTime(toString());
		dt.setTime(getTime()+(long)day*0x5265c00L);
		return dt;
	}

	public DateTime addMonth(int iMonth){
		DateTime dt=(DateTime)clone();
		GregorianCalendar gval=new GregorianCalendar();
		gval.setTime(dt);
		gval.add(2,iMonth);
		dt.setTime(gval.getTime().getTime());
		return dt;
	}

	public DateTime addYear(int iYear){
		DateTime dt=(DateTime)clone();
		GregorianCalendar gval=new GregorianCalendar();
		gval.setTime(dt);
		gval.add(1,iYear);
		dt.setTime(gval.getTime().getTime());
		return dt;
	}

	public DateTime addHour(int hour){
		DateTime dt=(DateTime)clone();
		dt.setTime(getTime()+(long)hour*3600000L);
		return dt;
	}

	public DateTime addMinute(int minute){
		DateTime dt=(DateTime)clone();
		dt.setTime(getTime()+(long)minute*60000L);
		return dt;
	}

	public DateTime addSecond(int second){
		DateTime dt=(DateTime)clone();
		dt.setTime(getTime()+(long)second*1000L);
		return dt;
	}

	public DateTime addMilliSecond(long mSecond){
		DateTime dt=(DateTime)clone();
		dt.setTime(getTime()+mSecond);
		return dt;
	}

	/** 将一个毫秒值转换为**小时**分钟**秒的形式 */
	public static String showTime(long time){
		if(time<1000){
			return time+"毫秒";
		}
		StringBuilder sb=new StringBuilder();

		long year=time/24/60/60/1000/365;
		if(year!=0){
			sb.append(year+"年");
		}
		time=time%(24*60*60*1000*365);

		long day=time/24/60/60/1000;
		if(day!=0){
			sb.append(day+"天");
		}

		time=time%(24*60*60*1000);
		long hour=time/60/60/1000;
		if(day!=0||hour!=0){
			sb.append(hour+"小时");
		}

		time=time%(60*60*1000);
		long min=time/60/1000;
		if(day!=0||hour!=0||min!=0){
			sb.append(min+"分钟");
		}

		time=time%(60*1000);
		long second=time/1000;
		if(day!=0||hour!=0||min!=0||second!=0){
			sb.append(second+"秒");
		}

		time=time%(1000);
		long ms=time;
		if((day!=0||hour!=0||min!=0||second!=0)&&ms!=0){
			sb.append(ms+"毫秒");
		}
		return sb.toString();
	}

	public Date toDate(){
		return new Date(this.getTime());
	}

	//每次都new一个SimpleDateFormat会占用过多内存,未每个线程造一个SimpleDateFormat
	private static Map<String,ThreadLocal<SimpleDateFormat>> map=new HashMap();

	public static SimpleDateFormat getFormat(final String pattern){
		ThreadLocal<SimpleDateFormat> t=map.get(pattern);
		if(t!=null){
			return t.get();
		}
		synchronized(DateTime.class){
			t=map.get(pattern);
			if(t!=null){
				return t.get();
			}

			t=new ThreadLocal<SimpleDateFormat>(){
				public SimpleDateFormat initialValue(){
					log.debug(Thread.currentThread().getName()+"创建dateformat:"+pattern);
					return new SimpleDateFormat(pattern);
				}
			};
			map.put(pattern,t);
			return t.get();
		}
	}

	/** 将微秒转为毫秒 */
	public static double toMs(double nano){
		double d=nano/(1000000);
		return StringUtil.round(d,4);
	}
}
