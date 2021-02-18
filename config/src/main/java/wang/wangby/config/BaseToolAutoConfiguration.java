package wang.wangby.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.HtmlUtils;
import wang.wangby.serialize.json.FastJsonImpl;
import wang.wangby.serialize.json.JsonUtil;
import wang.wangby.template.SimpleObjectConvertor;
import wang.wangby.template.TemplateUtil;
import wang.wangby.template.VelocityProperties;
import wang.wangby.utils.IdWorker;


@Configuration
@EnableConfigurationProperties(VelocityProperties.class)
public class BaseToolAutoConfiguration {
    @Bean
    public Beans beans(){
        return new Beans();
    }

    @Bean
    public JsonUtil fastJson() {
        return new FastJsonImpl();
    }

    @Bean
    public TemplateUtil templateUtil(VelocityProperties velocityProperties, JsonUtil jsonUtil) {
        return new TemplateUtil(velocityProperties,  new SimpleObjectConvertor() {
            //将对象转为json
            public String toJson(Object target) {
                return jsonUtil.toString(target);
            }
            //过滤html字符
            public String htmlEscape(String str) {
                return HtmlUtils.htmlEscape(str);
            }
        });
    }


    @Bean
    @ConditionalOnMissingBean
    public IdWorker defaultIdWorker(){
        return new IdWorker();
    }

}
