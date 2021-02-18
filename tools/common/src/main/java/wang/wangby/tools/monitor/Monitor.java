package wang.wangby.tools.monitor;
import java.util.Map;


public interface Monitor {
	/**
	 * 该监控器下所有统计项,以xxx=***的形式展现
	 * */
	Map<String,Object> getResult();
	
}




