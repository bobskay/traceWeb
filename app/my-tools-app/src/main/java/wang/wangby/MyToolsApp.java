package wang.wangby;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan(basePackages = {"wang.wangby"},annotationClass= Mapper.class)
public class MyToolsApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context= SpringApplication.run(MyToolsApp.class, args);
    }

}
