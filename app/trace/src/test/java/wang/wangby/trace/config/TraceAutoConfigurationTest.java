package wang.wangby.trace.config;


import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import wang.wangby.TraceApp;
import wang.wangby.repository.MysqlRepository;
import wang.wangby.repostory.Repository;
import wang.wangby.repostory.database.DDLUtil;
import wang.wangby.trace.model.Profit;
import wang.wangby.trace.model.StockOrder;
import wang.wangby.trace.model.TraceOrder;

import java.math.BigDecimal;

public class TraceAutoConfigurationTest {

    public static void main(String[] args) throws Exception {
       ApplicationContext context= SpringApplication.run(TraceApp.class, args);

        DDLUtil ddlUtil=context.getBean(DDLUtil.class);
       String sql= ddlUtil.getCreate(StockOrder.class);
        System.out.println("createSql:"+sql);

         sql= ddlUtil.getCreate(TraceOrder.class);
        System.out.println("createSql:"+sql);

        sql= ddlUtil.getCreate(Profit.class);
        System.out.println("createSql:"+sql);
//
//        MysqlRepository fp= (MysqlRepository) context.getBean(Repository.class);
//
//        StockOrder so=new StockOrder();
//        so.setPrice(new BigDecimal(100));
//        System.out.println("准备新增："+so);
//        fp.insert(so);
//
//        StockOrder o2= fp.get(StockOrder.class,so.getId());
//        System.out.println("新增完毕："+o2);
    }
}