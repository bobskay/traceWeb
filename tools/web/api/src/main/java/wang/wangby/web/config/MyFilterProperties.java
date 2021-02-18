package wang.wangby.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import wang.wangby.annotation.Remark;

@Data
@ConfigurationProperties("my.webfilter")
public class MyFilterProperties {

	@Remark("实例id")
	private String instanceId;

	@Remark("是否开启过滤器")
	private boolean enable=true;

	@Remark("是否统计访问耗时")
	public boolean statis = true;

	@Remark("是否开启排队规则")
	public boolean queue = true;

	@Remark("是否权限校验")
	public boolean permission = false;

	@Remark("忽略权限验证的请求")
	public String[] permissionExclude={"/login/prepareLogin","/login/login","/error","/permission/init"};

	// 格式化配置的url,因为配置文件不允许用/,所以配置的时候用-,还有路径必须以/开头
	public static String formatUrl(String url) {
		url = url.trim();
		if (!url.startsWith("/")) {
			url = "/" + url;
		}
		return url.replace("-", "/");
	}

}
