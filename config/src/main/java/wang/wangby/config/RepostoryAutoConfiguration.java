package wang.wangby.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import wang.wangby.annotation.persistence.Table;
import wang.wangby.base.entity.convertor.EntityTableConvertor;
import wang.wangby.repository.MysqlRepository;
import wang.wangby.repository.jdbc.RepositoryConfigHelper;
import wang.wangby.repository.jdbc.RoutingDataSource;
import wang.wangby.repostory.EntityDaoFinder;
import wang.wangby.repostory.config.RepostoryProperties;
import wang.wangby.repostory.database.DDLUtil;
import wang.wangby.repostory.selector.QueryUtil;
import wang.wangby.template.TemplateUtil;
import wang.wangby.utils.ClassUtil;
import wang.wangby.utils.IdWorker;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.HashMap;

@Configuration
@Import(BaseToolAutoConfiguration.class)
@ConditionalOnClass(SqlSessionFactory.class)
@EnableConfigurationProperties(RepostoryProperties.class)
@Slf4j
public class RepostoryAutoConfiguration {

    @Bean
    public RoutingDataSource routingDataSource() {
        RoutingDataSource ds = new RoutingDataSource(null, new HashMap<>());
        return ds;
    }

    @Bean
    public MysqlRepository mysqlRepository() {
        return new MysqlRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public EntityTableConvertor defaultEntityTableConvertor() {
        return new EntityTableConvertor() {
            @Override
            public String getTableName(Class clazz) {
                Table tb = ClassUtil.getAnnotation(clazz, Table.class);
                if (tb != null) {
                    return tb.value();
                }
                return "t_" + clazz.getSimpleName().toLowerCase();
            }
        };
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource routingDataSource, RepostoryProperties repostoryProperties, TemplateUtil templateUtil, EntityTableConvertor entityTableConvertor) throws Exception {
        log.debug("准备创建SqlSessionFactory,数据源={},系统配置={}", routingDataSource.getClass().getSimpleName(), repostoryProperties);
        RepositoryConfigHelper config = new RepositoryConfigHelper(routingDataSource,repostoryProperties,templateUtil,entityTableConvertor);
        SqlSessionFactory sqlSessionFactory = config.createSqlSessionFactory();
        return sqlSessionFactory;
    }

    @Bean
    public IdWorker idWorker(RepostoryProperties repostoryProperties) throws NoSuchFieldException, IllegalAccessException {
        IdWorker idWorker = new IdWorker(repostoryProperties.getMachineNo());
        Field field = IdWorker.class.getDeclaredField("INSTANCE");
        field.setAccessible(true);
        field.set(IdWorker.class, idWorker);
        log.info("初始化主键生成器成功,当前机器号:" + repostoryProperties.getMachineNo());
        return idWorker;
    }

    @Bean
    public DDLUtil ddlUtil(TemplateUtil templateUtil) {
        return new DDLUtil();
    }

    @Bean
    public EntityDaoFinder entityDaoFinder(Beans beans) {
        return new EntityDaoFinder(c -> beans.getEntityDao(c));
    }

    @Bean
    public QueryUtil queryUtil() {
        return new QueryUtil();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(RoutingDataSource routingDataSource) {
        return new JdbcTemplate(routingDataSource);
    }

}
