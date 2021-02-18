package wang.wangby.repository.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import wang.wangby.log.LogUtil;
import wang.wangby.repostory.database.dto.DatabaseInfo;
import wang.wangby.repostory.database.dto.DbType;
import wang.wangby.utils.DateTime;

import javax.sql.DataSource;

@Slf4j
public class DbUtils {

    public static DataSource createDerby(String databaseDir) {
        if (databaseDir == null) {
            String WORK_DIR = System.getProperty("user.dir") + "/target";
            databaseDir = WORK_DIR + "/testDB";
        }
        DatabaseInfo info = new DatabaseInfo();
        info.setUrl("jdbc:derby:" + databaseDir + ";create=true;");
        info.setDbType(DbType.derby + "");

        log.debug("准备连接数据库:" + info);
        long time = System.currentTimeMillis();
        DataSource ds = createDatasource(info);
        long consumer = System.currentTimeMillis() - time;
        log.debug("创建数据库耗时:" + DateTime.showTime(consumer));

        return ds;
    }

    public static DataSource createDatasource(DatabaseInfo info) {
        DbType dbType = DbType.valueOf(info.getDbType().toLowerCase());
        log.info("新建连接池:" + info.getKey());
        DruidDataSource dd = new DruidDataSource();
        dd.setUsername(info.getUsername());
        dd.setPassword(info.getPassword());
        dd.setDriverClassName(dbType.getDriverClassName());
        dd.setUrl(info.getUrl());
        dd.setMaxActive(3);
        dd.setMaxWait(2000);//默认为-1,会一直尝试
        try {
            dd.getConnection();
        } catch (Exception ex) {
            dd.close();
            log.error(ex.getMessage(), ex);
            throw new RuntimeException("获取数据库链接失败:" + LogUtil.getCause(ex).getMessage());
        }
        return dd;
    }
}
