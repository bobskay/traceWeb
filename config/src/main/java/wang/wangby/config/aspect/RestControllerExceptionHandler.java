package wang.wangby.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import wang.wangby.entity.request.Response;
import wang.wangby.exception.Message;
import wang.wangby.log.LogUtil;


@Aspect
@Slf4j
public class RestControllerExceptionHandler {

	// 对于所有返回类型是Response对象的方法,如果执行出错了就封装一下
	@Pointcut("execution(wang.wangby.entity.request.Response *.*(..))")
	private void responseMethod() {
	}

	@Around("responseMethod()")
	public Object process(ProceedingJoinPoint pjp) throws Throwable {
		try {
			return pjp.proceed();
		}catch(Exception ex) {
			Throwable th= LogUtil.getCause(ex);
			if(!(th instanceof Message)) {
				log.error("未知异常",ex);
			}else{
				log.debug(th.getMessage());
			}
			return Response.fail(th.getMessage());
		}
	}

}
