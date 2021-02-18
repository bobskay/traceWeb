package wang.wangby.trace.config;


import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import wang.wangby.TraceApp;
import wang.wangby.persistence.file.FileRepository;
import wang.wangby.repostory.Repository;
import wang.wangby.trace.model.StockOrder;

import java.math.BigDecimal;

public class TraceAutoConfigurationTest {

    public static void main(String[] args) throws Exception {
       ApplicationContext context= SpringApplication.run(TraceApp.class, args);

        FileRepository fp= (FileRepository) context.getBean(Repository.class);
        StockOrder so=new StockOrder();
        so.setPrice(new BigDecimal(100));
        fp.insert(so);

        StockOrder o2= fp.get(StockOrder.class,so.getId());
        System.out.println(o2);
    }
}