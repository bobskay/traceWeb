package wang.wangby.tools.monitor.dto;


import wang.wangby.Constants;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 统计访问的耗时
 * */
public class RequestStatist {
	private String url;
	private AtomicLong totalConsume = new AtomicLong(0);
	private AtomicLong count = new AtomicLong(0);
	private List<Long> accuracy;
	private AtomicLong[] accuracyCount;

	public RequestStatist(String url, List<Long> accuracy) {
		this.url = url;
		this.accuracy = accuracy;
		accuracyCount = new AtomicLong[accuracy.size() + 1];
		for (int i = 0; i < accuracyCount.length; i++) {
			accuracyCount[i] = new AtomicLong(0);
		}
	}

	public void add(long consume) {
		totalConsume.addAndGet(consume);
		count.incrementAndGet();
		int i = 0;
		for (; i < accuracy.size(); i++) {
			if (consume < accuracy.get(i)) {
				accuracyCount[i].incrementAndGet();
				return;
			}
		}
		accuracyCount[i].incrementAndGet();
	}

	public String getUrl() {
		return url;
	}

	public String statist() {
		StringBuilder sb = new StringBuilder();
		sb.append(url + "_totalConsume" + ":" + totalConsume.get());
		sb.append(Constants.LINE);
		sb.append(url + "_count:" + count.get());
		sb.append(Constants.LINE);
		int i = 0;
		for (; i < accuracy.size(); i++) {
			sb.append(url + "_consume" + accuracy.get(i) + ":" + accuracyCount[i].get());
			sb.append(Constants.LINE);
		}
		sb.append(url + "_consumegt" + accuracy.get(i-1)+ ":" + accuracyCount[i].get());
		return sb.toString();
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

}
