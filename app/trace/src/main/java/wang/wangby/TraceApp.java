package wang.wangby;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import wang.wangby.trace.config.MarketConfig;

import java.io.File;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class TraceApp implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication.run(TraceApp.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        if(MarketConfig.test){
            log.info("当前是测试环境不会下单");
        }else{
            log.info("当前是正式环境！！！！");
        }
    }
}
