package wang.wangby.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import wang.wangby.template.TemplateUtil;
import wang.wangby.tools.monitor.TimeMonitor;
import wang.wangby.utils.StringUtil;
import wang.wangby.web.MenuProvider;
import wang.wangby.web.config.MyFilterProperties;
import wang.wangby.web.webfilter.MyFilter;
import wang.wangby.web.webfilter.QueueFilter;
import wang.wangby.web.webfilter.StatistFilter;
import wang.wangby.web.webfilter.permission.AdminUser;
import wang.wangby.web.webfilter.permission.PermissionFilter;
import wang.wangby.web.webfilter.permission.PermissionUser;
import wang.wangby.web.webfilter.permission.PermissionUserService;

import java.util.*;

@Configuration
@EnableConfigurationProperties(MyFilterProperties.class)
@Import({BaseToolAutoConfiguration.class,WebAutoConfiguration.class})
@Slf4j
public class MyFilterAutoConfiguration {

    @Autowired
    Environment environment;
    @Autowired
    Beans beans;

    @Bean
    @ConditionalOnMissingBean
    public PermissionUserService adminUserService(){
        return new PermissionUserService() {
            @Override
            public PermissionUser getUser(String username) {
                return new AdminUser();
            }
        };
    }

    @Bean
    public MyFilter myFilter(TemplateUtil templateUtil, MenuProvider menuProvider, MyFilterProperties myFilterProperties, PermissionUserService permissionUserService, RequestMappingHandlerMapping mappingHandlerMapping) {
        String instanceId=environment.getProperty("eureka.instance.instance-id");
        if(instanceId==null){
            instanceId=myFilterProperties.getInstanceId();
        }

        log.debug("创建自定义过滤器:" + myFilterProperties);
        List webFilters = new ArrayList();
        if (myFilterProperties.isPermission()) {
            PermissionFilter permissionFilter = new PermissionFilter(templateUtil,menuProvider, permissionUserService, myFilterProperties.getPermissionExclude());
            webFilters.add(permissionFilter);
            log.debug("添加权限过滤器,忽略页面" + Arrays.asList(myFilterProperties.getPermissionExclude()));
        }

        TimeMonitor timeMonitor=beans.newTimeMonitor("httpRequest");
        if (myFilterProperties.statis) {
            webFilters.add(new StatistFilter(new ArrayList<>(),timeMonitor));
            log.info("开启耗时统计");
        }
        if (myFilterProperties.queue) {
            QueueFilter f = new QueueFilter(new HashMap<>());
            webFilters.add(f);
            log.info("开启请求排队");
        }

        log.debug("创建自定义过滤器:" + myFilterProperties);

        //只过滤配置了RequestMapping的链接
        Map<RequestMappingInfo, HandlerMethod> map = mappingHandlerMapping.getHandlerMethods();
        TreeSet set = new TreeSet();//所有可用的url
        for (RequestMappingInfo info : map.keySet()) {
            Set<String> patterns = info.getPatternsCondition().getPatterns();
            set.addAll(patterns);
        }
        log.debug("自定义过滤器可处理的url:" + set);
        MyFilter filter = new MyFilter(webFilters, url -> {
            return set.contains(url);
        }, instanceId);
        return filter;
    }


    private List<String> getFormatUrl(String url) {
        List list = new ArrayList();
        url = url.trim();
        if (StringUtil.isEmpty(url)) {
            return list;
        }
        for (String s : url.split(",")) {
            String x = s.trim();
            if (StringUtil.isNotEmpty(x)) {
                if (!x.startsWith("/")) {
                    x = "/" + x;
                }
                list.add(x);
            }
        }
        return list;
    }
}
