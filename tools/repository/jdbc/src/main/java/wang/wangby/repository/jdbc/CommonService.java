package wang.wangby.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import wang.wangby.repostory.database.SqlUtil;
import wang.wangby.repostory.database.dto.TableInfo;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CommonService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<TableInfo> getTables(){
        String sql="SHOW TABLES";
        List<TableInfo> list=new ArrayList();
        jdbcTemplate.query(sql,rs->{
            String tableName=rs.getString(1);
            String showTable="SHOW CREATE TABLE "+tableName;
            jdbcTemplate.query(showTable,create->{
                String createSql=create.getString(2);
                try{
                    TableInfo info= SqlUtil.toTable(createSql);
                    list.add(info);
                }catch (Exception ex){
                    log.warn("获得表信息出错,忽略"+tableName+":"+ex.getMessage());
                }
            });

        });
        return list;
    }
}
