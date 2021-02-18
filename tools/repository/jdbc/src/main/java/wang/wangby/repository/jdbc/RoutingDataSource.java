package wang.wangby.repository.jdbc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import wang.wangby.repostory.database.dto.DatabaseInfo;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {
    public static final String DEFAULT = "default";
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>() {
        protected String initialValue() {
            return DEFAULT;
        }
    };

    public static void setDB(String key) {
        contextHolder.set(key);
    }

    private Map<Object, DataSource> datasource;
    @Getter
    private Map<String, DatabaseInfo> configMap;

    public RoutingDataSource(DataSource defaultTargetDataSource, Map<String, DatabaseInfo> databaseInfo) {
        datasource=new HashMap<>();
        databaseInfo.forEach((key,info)->{
            DataSource ds= DbUtils.createDatasource(info);
            datasource.put(key,ds);
        });

        this.setDefaultTargetDataSource(defaultTargetDataSource);
        this.setTargetDataSources((Map) datasource);
        this.configMap=databaseInfo;
    }

    public DataSource addDtasource(String key, DatabaseInfo info) {
        info.setAlias(key);
        log.debug("新增数据库:"+info.getKey());
        DataSource dataSource=DbUtils.createDatasource(info);
        if (datasource.isEmpty()) {
            log.info("初始化默认数据源:" + key);
            datasource.put(DEFAULT, dataSource);
        }
        if(datasource.containsKey(key)){
            throw new RuntimeException("数据源已经存在:"+key);
        }
        configMap.put(key,info);
        datasource.put(key, dataSource);
        super.afterPropertiesSet();
        return dataSource;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return contextHolder.get();
    }

    public boolean isEmpty() {
        return datasource.isEmpty();
    }

}