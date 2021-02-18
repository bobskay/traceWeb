package wang.wangby.tools.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import wang.wangby.config.BaseToolAutoConfiguration;
import wang.wangby.config.Beans;
import wang.wangby.tools.shell.LongTimeShellPool;
import wang.wangby.tools.shell.ShellClient;
import wang.wangby.tools.thread.ThreadPool;
import wang.wangby.tools.thread.ThreadPoolConfig;

@Configuration
@Import(BaseToolAutoConfiguration.class)
@ComponentScan("wang.wangby.tools.controller")
public class MyToolsAutoConfiguration {

    @Bean
    public ShellClient shellClient(){
        return new ShellClient(60*10,"logManager");
    }

    @Bean
    public LongTimeShellPool longTimeShellPool(ShellClient shellClient, Beans beans){
        ThreadPool threadPool=beans.newThreadPool(ThreadPoolConfig.newInstance("LongTimeShellPool",10));
        return new LongTimeShellPool(shellClient,60,threadPool);
    }
}
