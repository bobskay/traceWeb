package wang.wangby.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import wang.wangby.config.aspect.HitMonitorAspect;
import wang.wangby.permission.config.PermissionProperties;
import wang.wangby.permission.entity.LoginUser;
import wang.wangby.permission.service.LoginService;
import wang.wangby.repository.jdbc.RoutingDataSource;
import wang.wangby.repostory.database.dto.DatabaseInfo;
import wang.wangby.tools.cache.CacheConfig;
import wang.wangby.tools.cache.HashMapCache;
import wang.wangby.tools.thread.ThreadPool;
import wang.wangby.web.webfilter.permission.PermissionUser;
import wang.wangby.web.webfilter.permission.PermissionUserService;

import javax.sql.DataSource;

@Configuration
@ComponentScan("wang.wangby.permission")
@Import(RepostoryAutoConfiguration.class)
@EnableConfigurationProperties(PermissionProperties.class)
@ConditionalOnClass(LoginService.class)
@Slf4j
public class PermissionAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "my.datasource.permission")
    public DatabaseInfo permissionDbInfo() throws IllegalArgumentException {
        return new DatabaseInfo();
    }

    @Bean
    public DataSource permissionDatasource(DatabaseInfo permissionDbInfo, RoutingDataSource routingDataSource) throws IllegalArgumentException {
        return routingDataSource.addDtasource("permission",permissionDbInfo);
    }

    @Bean
    public HashMapCache<String, LoginUser> loginUserCache(){
        CacheConfig cacheConfig=new CacheConfig();
        cacheConfig.setName("loginUser");
        cacheConfig.setScanInterval(60);
        cacheConfig.setDefaultSurvival(60*30);
        return HashMapCache.createCache(cacheConfig);
    }

    @Bean
    public HitMonitorAspect hitMonitorAspect(Beans beans){
        ThreadPool threadPool=beans.newThreadPool("HitMonitorAspect",10);
        return new HitMonitorAspect(threadPool);
    }

    @Bean
    public PermissionUserService permissionUserService(LoginService loginService) {
        return new PermissionUserService() {
            @Override
            public PermissionUser getUser(String userName) {
                return loginService.getLoginUser(userName);
            }
        };
    }



}
