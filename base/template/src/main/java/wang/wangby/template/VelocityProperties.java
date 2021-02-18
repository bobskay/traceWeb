package wang.wangby.template;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import wang.wangby.Constants;

@Data
@ConfigurationProperties("my.velocity")
public class VelocityProperties {
	private String encoding= Constants.UTF8;
	private String root="/templates";//模板文件保存的根路径
	private String log="/opt/logs/velocity.log";
}
