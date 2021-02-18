package wang.wangby.entity.request;

import lombok.Data;
import wang.wangby.annotation.Remark;

@Data
@Remark("封装请求的对象")
public class Request<T> {

	@Remark("权限标识")
	private String token;
	@Remark("请求唯一标识")
	private String requestId;
	@Remark("具体的请求参数")
	private T content;

	

}
