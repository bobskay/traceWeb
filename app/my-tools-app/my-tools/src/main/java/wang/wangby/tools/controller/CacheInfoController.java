package wang.wangby.tools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.web.Menu;
import wang.wangby.config.Beans;
import wang.wangby.tools.controller.vo.CacheDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("cacheInfo")
public class CacheInfoController extends ToolController {

    @Autowired
    Beans beans;

    @Menu("缓存信息")
    @RequestMapping("index")
   public String index(){
        List<CacheDetail> details=new ArrayList<>();
       beans.getHitMonitorMap().values().forEach(hitMonitor -> {
           CacheDetail detail=new CacheDetail();
           Set keys=hitMonitor.getDataMap().keySet();
           List list=new ArrayList(keys);
           Collections.sort(list);

           detail.setKeys(list);
           detail.setName(hitMonitor.getName());
           detail.setTotal(hitMonitor.getTotal());
           details.add(detail);
        });
        return $("index",details);
   }
}
