package wang.wangby.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wang.wangby.web.MenuProvider;
import wang.wangby.web.config.IndexPageProperties;
import wang.wangby.web.utils.DefaultMenuProvider;

@Slf4j
@EnableConfigurationProperties(IndexPageProperties.class)
@Configuration
public class WebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MenuProvider defaultMenuProvider(){
        return new DefaultMenuProvider();
    }
}
