package wang.wangby.tools.monitor;

import wang.wangby.tools.monitor.dto.TimeStatist;
import wang.wangby.tools.thread.ThreadPool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


//监控某个执行耗时
public class TimeMonitor implements MonitorData {
	//默认统计粒度
	public static final long[] DEFLUT_ACCURACY=new long[]{5,10,100,500,1000,5000};

	private ThreadPool threadPool;
	private Map<String, TimeStatist> map=new ConcurrentHashMap<>();
	private Set<String> keys=new HashSet();
	private TimeStatist total;
	private String name;
	private long[] accuracy;

	//name,名称
	//accuracy 统计唯独
	//coreSize,统计用的线程数
	public TimeMonitor(String name, long[] accuracy, ThreadPool threadPool){
		if(accuracy==null){
			accuracy=DEFLUT_ACCURACY;
		}
		this.name=name;
		this.accuracy= Arrays.copyOf(accuracy,accuracy.length);
		total=new TimeStatist(name+".totalconsume",Arrays.copyOf(accuracy,accuracy.length));
		map.put(name+".totalrequest",total);
		this.threadPool=threadPool;
	}

	/**
	 * 新增一次请求
	 *
	 * @param uri 请求的uri
	 * @prram consume 耗时
	 */
	public void addRequest(String uri,long consume){
		RequestEvent event=new RequestEvent(uri,consume,this);
		threadPool.run(event);
	}


	public TimeStatist get(String name){
		return map.get(name);
	}

	public Set<String> getKeys(){
		return keys;
	}

	public long[] getAccuracy(){
		return this.accuracy;
	}

	public Map<String,TimeStatist> getMap(){
		return map;
	}

	public TimeStatist getTotal(){
		return total;
	}


	@Override
	public String getName() {
		return "timeMonitor_"+name;
	}

	@Override
	public Map<Object, Object> getDataMap() {
		Set<Map.Entry<String,TimeStatist>> en=map.entrySet();
		Map map= Collections.synchronizedMap(new LinkedHashMap());
		for(Map.Entry<String,TimeStatist> e:en){
			map.putAll(e.getValue().statist());
		}
		return map;
	}
}
