package wang.wangby.entity.request;

import lombok.Getter;
import lombok.Setter;
import wang.wangby.annotation.Remark;

@Getter
@Setter
@Remark("统一的json返回格式")
public class Response<T> {
	public static final int SUCCESS=200;//成功
	public static final int TIME_OUT=408;//请求超时
	public static final int INTERNAL_SERVER_ERROR=500;//内部错误
	public static final int NOT_FOUND=404;//内部错误
	public static final int INTERNAL_INVOKE_TIMEOUT=501;//内部调用超时

	@Remark("方法的实际返内容")
	private T data;
	@Remark("请求唯一标识")
	private String requestId;
	@Remark("异常信息")
	private String message;
	@Remark("错误号,200代表成功")
	private Integer code;
	@Remark("请求状态,0已发送,1执行中,2执行完毕")
	private Integer state;
	@Remark("版本号")
	private String version;
	@Remark("请求产生的异常,内部传输用,生成json的时候会忽略")
	private transient Throwable error;


	public static Response success(Object data) {
		Response resp = new Response();
		resp.setState(RequestState.FINISH.ordinal());
		resp.data = data;
		resp.code=200;
		return resp;
	}

	public static Response fail(String error) {
		Response resp = new Response();
		resp.setState(RequestState.FINISH.ordinal());
		resp.data = null;
		resp.code=500;
		resp.message = error;
		return resp;
	}

	public static Response notFound(String url) {
		Response resp = new Response();
		resp.setState(RequestState.FINISH.ordinal());
		resp.data = null;
		resp.code=NOT_FOUND;
		resp.message = url;
		return resp;
	}

	public static Response timeout(String error) {
		Response resp = new Response();
		resp.setState(RequestState.FINISH.ordinal());
		resp.code=INTERNAL_INVOKE_TIMEOUT;
		resp.message = error;
		return resp;
	}

	public boolean isSuccess(){
		return code==SUCCESS;
	}
	//请求超时
	public boolean requestTimeout(){
		return code== TIME_OUT;
	}
}
