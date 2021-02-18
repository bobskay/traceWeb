package wang.wangby.web.webfilter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import wang.wangby.tools.monitor.MonitorData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
//让请求排队的过滤器
public class QueueFilter implements WebFilter<String>, MonitorData {
	private Map<String, ArrayBlockingQueue> queueMap;
	

	public QueueFilter(Map<String, Integer> countMap) {
		log.info("开启并发控制:"+countMap);
		this.queueMap=new ConcurrentHashMap<>();
	}
	
	
	public Map<String,Object> getQueueInfo(){
		Map map=Collections.synchronizedMap(new LinkedHashMap());
		queueMap.forEach((key,value)->{
			QueueInfo info=new QueueInfo();
			info.size=value.size();
			info.remain=value.remainingCapacity();
			map.put(key+"_queue_running",info.size);
			map.put(key+"_queue_remain",info.remain);
		});
		return map;
	}

	@Override
	public String begin(HttpServletRequest request, HttpServletResponse httpResponse) throws Exception {
		String uri = request.getRequestURI();
		BlockingQueue queue = queueMap.get(uri);
		if(queue==null||queue.offer(uri)) {
			return "";
		}
		String contentType=request.getContentType();
		if(contentType==null){
			contentType="";
		}
		if(contentType.indexOf("json")!=-1){
			httpResponse.setContentType("application/json; charset=utf-8");
			httpResponse.getWriter().write("{\"message\":\""+"请求已达上线:"+queue.size()+"\"}");
		}else{
			httpResponse.setContentType("text/html;charset=UTF-8");
			httpResponse.getWriter().write("请求已达上线:"+queue.size());
		}
		return null;
	}


	@Override
	public void end(String t, HttpServletRequest request) throws InterruptedException {
		String uri = request.getRequestURI();
		BlockingQueue queue = queueMap.get(uri);
		if(queue!=null) {
			queue.poll();
		}
	}

	@Override
	public String getName() {
		return "queueFilter";
	}

	@Override
	public Map getDataMap() {
		return getQueueInfo();
	}


	@Data
	public class QueueInfo{
		private int remain;
		private int size;
		public String toString() {
			return size+"/"+(size+remain);
		}
	}
	

}
