package wang.wangby.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import wang.wangby.annotation.Remark;

@Data
@ConfigurationProperties("my.web.index")
public class IndexPageProperties {
    @Remark("标题")
    private String title="${my.web.index.title}";
    @Remark("首页加载的url")
    private String indexContentUrl;
    @Remark("静态文件地址")
    private String staticRoot="/resources";
    @Remark("项目基地址")
    private String webRoot="";
    @Remark("是否展开菜单")
    private boolean expandAll=true;
}
