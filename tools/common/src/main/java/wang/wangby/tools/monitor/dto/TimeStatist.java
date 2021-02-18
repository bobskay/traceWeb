package wang.wangby.tools.monitor.dto;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 耗时统计
 * @author ming
 */
public class TimeStatist {
	private String name;
	private  AtomicLong totalConsume=new AtomicLong(0);//总耗时
	private  AtomicLong count=new AtomicLong(0);//总次数
	private long[] accuracy;
	private  AtomicLong[] accuracyCount; //保存具体值
	
	public TimeStatist(String name,long[] accuracy){
		this.name=name;
		this.accuracy=Arrays.copyOf(accuracy, accuracy.length);
		accuracyCount=new AtomicLong[accuracy.length+1];
		for(int i=0;i<accuracyCount.length;i++){
			accuracyCount[i]=new AtomicLong(0);
		}
	}
	public void add(long consume){
		totalConsume.addAndGet(consume);
		count.incrementAndGet();
		int i=0;
		for(;i<accuracy.length;i++){
			if(consume<accuracy[i]){
				accuracyCount[i].incrementAndGet();
				return;
			}
		}
		accuracyCount[i].incrementAndGet();
	}

	public String getName() {
		return name;
	}

	public Map<String,Long> statist() {
		Map map=Collections.synchronizedMap(new LinkedHashMap());
		map.put(name+"_totalConsume", totalConsume.get());
		map.put(name+"_count",count.get());
		int i=0;
		for(;i<accuracy.length;i++){
			map.put(name+"_consume"+accuracy[i],+accuracyCount[i].get());
		}
		map.put(name+"_consumegt"+accuracy[i-1],accuracyCount[i].get());
		return map;
	}
	public AtomicLong getTotalConsume() {
		return totalConsume;
	}
	public AtomicLong getCount() {
		return count;
	}
	public AtomicLong[] getAccuracyCount() {
		return accuracyCount;
	}
	public long[] getAccuracy() {
		return accuracy;
	}
	
	
	
}




