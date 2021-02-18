package wang.wangby.repository;

import lombok.SneakyThrows;
import org.apache.ibatis.session.SqlSessionFactory;
import wang.wangby.base.entity.convertor.PrefixName;
import wang.wangby.repository.jdbc.RepositoryConfigHelper;
import wang.wangby.repository.jdbc.RoutingDataSource;
import wang.wangby.repostory.config.RepostoryProperties;
import wang.wangby.repostory.database.dto.DatabaseInfo;
import wang.wangby.template.TemplateUtil;

import java.util.HashMap;

public class DbTestUtil {
    public final static SqlSessionFactory sqlSessionFactory=getSqlSessionFactory();

    @SneakyThrows
    public static SqlSessionFactory getSqlSessionFactory() {
        DatabaseInfo databaseInfo=new DatabaseInfo();
        databaseInfo.setUrl("jdbc:mysql://192.168.2.11:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false");
        databaseInfo.setUsername("root");
        databaseInfo.setPassword("123456");

        RoutingDataSource routingDataSource=new RoutingDataSource(null,new HashMap<>());
        routingDataSource.addDtasource("test",databaseInfo);

        RepostoryProperties repostoryProperties=new RepostoryProperties();
        TemplateUtil templateUtil=new TemplateUtil();

        RepositoryConfigHelper helper=new RepositoryConfigHelper(routingDataSource,
                repostoryProperties,templateUtil,new PrefixName("t_"));
        SqlSessionFactory factory=helper.createSqlSessionFactory();
        return factory;
    }
}
