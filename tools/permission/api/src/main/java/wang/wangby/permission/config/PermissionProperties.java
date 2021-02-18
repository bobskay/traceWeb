package wang.wangby.permission.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("my.permission")
public class PermissionProperties {
    //登录过期时间
    private long loginExpireSecond=60*24*7;
}
