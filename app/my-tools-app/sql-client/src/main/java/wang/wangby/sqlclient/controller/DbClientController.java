package wang.wangby.sqlclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.repository.jdbc.CommonService;
import wang.wangby.repository.jdbc.RoutingDataSource;
import wang.wangby.repostory.database.dto.TableInfo;
import wang.wangby.web.controller.BaseController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("dbClient")
@RestController
public class DbClientController extends BaseController {
    @Autowired
    private RoutingDataSource routingDataSource;
    @Autowired
    CommonService commonService;

    @RequestMapping("index")
    public String index(String alias){
        RoutingDataSource.setDB(alias);
        List<TableInfo> tableInfos=commonService.getTables();
        Map map=new HashMap<>();
        map.put("tableInfoList",tableInfos);
        return $("index",map);
    }
}
