package wang.wangby.tools.monitor;

import wang.wangby.utils.StringUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


/**次数监控*/
public class CountMonitor implements Monitor{
	private  AtomicLong count=new AtomicLong(0);
	private String name;
	//<String,AtomicLong>
	private Map map=new ConcurrentHashMap();
	
	private boolean increase;
	public CountMonitor(String name){
		if(StringUtil.isEmpty(name)) {
			throw new RuntimeException("名称不能为空");
		}
		this.name=name;
		map.put(name, count);
		increase=true;
	}
	
	public CountMonitor(String name,boolean increase){
		if(StringUtil.isEmpty(name)) {
			throw new RuntimeException("名称不能为空");
		}
		this.name=name;
		map.put(name, count);
		this.increase=increase;
	}
	
	public String toStr() {
		return name+"="+count.get();
	}
	
	public void increase(long delta){
		if(!increase) {
			count.set(delta);
		}else {
			count.addAndGet(delta);
		}
		
	}

	
	
	public boolean isIncrease() {
		return increase;
	}

	@Override
	public Map<String, Object> getResult() {
		return map;
	}

	public String getName() {
		return name;
	}

	public AtomicLong getCount() {
		return count;
	}
	
	
}
