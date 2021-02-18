package wang.wangby;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import wang.wangby.zk.config.ZkManagerAutoConfiguration;

@SpringBootApplication
@Import(ZkManagerAutoConfiguration.class)
public class ZkManagerApp{
    public static void main(String args[]){
        SpringApplication.run(ZkManagerApp.class, args);
    }


}