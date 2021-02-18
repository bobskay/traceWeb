package wang.wangby.sqlclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.web.Menu;
import wang.wangby.entity.request.Response;
import wang.wangby.repository.jdbc.RoutingDataSource;
import wang.wangby.repostory.database.dto.DatabaseInfo;
import wang.wangby.repostory.database.dto.DbType;
import wang.wangby.web.controller.BaseController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("dbConfig")
@RestController
public class DbConfigController extends BaseController {
    @Autowired
    private RoutingDataSource routingDataSource;

    @RequestMapping("index")
    @Menu("数据库管理")
    public String index(){
        Map map=new HashMap<>();
        map.put("databaseInfoList",routingDataSource.getConfigMap().values());
        return $("index",map);
    }

    @RequestMapping("prepareInsert")
    public String prepareInsert(){
        Map map=new HashMap();
        map.put("dbTypeList", DbType.values());
        return $("prepareInsert");
    }

    @RequestMapping("insert")
    public Response<String> insert(@RequestBody DatabaseInfo databaseInfo){
        routingDataSource.addDtasource(databaseInfo.getKey(),databaseInfo);
        return respone("ok");
    }
}
