package wang.wangby.web.webfilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebFilter<T> {

	//请求开始,这个放回值回传到end里面.特别的当返回的是null的时候,就会中断过滤器
	T begin(HttpServletRequest request, HttpServletResponse httpResponse) throws Exception;

	//访问结束
	void end(T t, HttpServletRequest request)  throws Exception;
	
	
	
}
