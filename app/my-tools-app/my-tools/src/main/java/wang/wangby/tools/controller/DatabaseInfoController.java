package wang.wangby.tools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.web.Menu;
import wang.wangby.base.entity.EntityInfo;
import wang.wangby.config.Beans;
import wang.wangby.repository.jdbc.RoutingDataSource;
import wang.wangby.repostory.database.DDLUtil;
import wang.wangby.repostory.database.dto.TableInfo;
import wang.wangby.tools.controller.vo.EntityTable;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/databaseInfo")
public class DatabaseInfoController extends ToolController {

    @Autowired
    RoutingDataSource routingDataSource;
    @Autowired
    Beans beans;
    @Autowired
    DDLUtil ddlUtil;

    @RequestMapping("/index")
    @Menu("数据库信息")
    public String index() {
        List<EntityTable> modelTables=new ArrayList<>();
        for(Class clazz:beans.getEntities()){
            TableInfo table=ddlUtil.toTableInfo(clazz);
            EntityInfo modelInfo=EntityInfo.getInstance(clazz);

            EntityTable mt=new EntityTable();
            mt.setTableInfo(table);
            mt.setEntityInfo(modelInfo);
            mt.setCreateSql(ddlUtil.getCreate(clazz));

            modelTables.add(mt);
        }
        return $("index",modelTables);
    }
}
