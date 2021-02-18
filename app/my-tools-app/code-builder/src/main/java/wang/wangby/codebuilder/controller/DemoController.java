package wang.wangby.codebuilder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.Remark;
import wang.wangby.codebuilder.controller.vo.CodeConfig;
import wang.wangby.codebuilder.model.DemoModel;
import wang.wangby.codebuilder.service.DemoService;
import wang.wangby.codebuilder.utils.CodeCreator;
import wang.wangby.entity.Pagination;
import wang.wangby.entity.request.Response;
import wang.wangby.utils.IdWorker;
import wang.wangby.web.controller.BaseController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/demo")
@RestController
@Remark("演示程序")
public class DemoController extends BaseController {
    private Map<String, CodeConfig> configMap = new ConcurrentHashMap<>();

    @Autowired
    DemoService demoService;

    @RequestMapping("setConfig")
    public Response<String> setConfig(@RequestBody CodeConfig codeConfig) {
        String id = IdWorker.nextString();
        configMap.put(id, codeConfig);
        return respone(id);
    }


    @RequestMapping("webIndex")
    public String webIndex(String configId) {
        CodeConfig codeConfig = configMap.get(configId);
        Map map = new HashMap();
        map.put("webroot", "");
        map.put("title", codeConfig.getMenuName());
        map.put("codeConfig", codeConfig);
        return $("webIndex", map);
    }

    @RequestMapping("index")
    public String index(@RequestBody CodeConfig codeConfig) {
        CodeCreator codeCreator = new CodeCreator(codeConfig.getSql(), codeConfig.getPackageName());
        Map map=new HashMap();
        map.put("codeConfig",codeConfig);
        map.put("codeCreator",codeCreator);
        return $("index", map);
    }

    @RequestMapping("select")
    public Response<Pagination> select(HttpServletRequest request) {
        String sql=request.getParameter("sql");
        String limit=request.getParameter("limit");
        String offset=request.getParameter("offset");
        DemoModel demoModel=new DemoModel();
        demoModel.setSql(sql);
        Pagination page = demoService.selectPage(demoModel,Integer.parseInt(offset), Integer.parseInt(limit));
        return respone(page);
    }



    @RequestMapping("/prepareInsert")
    @Remark("进入新增页面")
    public String prepareInsert(String sql) {
        CodeCreator codeCreator = new CodeCreator(sql, "");
        Map map=new HashMap();
        map.put("codeCreator",codeCreator);
        return $("prepareInsert", map);
    }


}
