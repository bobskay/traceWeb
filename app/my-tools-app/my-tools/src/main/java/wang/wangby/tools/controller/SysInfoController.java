package wang.wangby.tools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import wang.wangby.annotation.Remark;
import wang.wangby.entity.request.Response;
import wang.wangby.web.MenuProvider;
import wang.wangby.web.dto.MenuInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@RestController
@RequestMapping("/sysInfo")
public class SysInfoController extends ToolController {

    @Autowired
    MenuProvider menuProvider;
    @Autowired
    ApplicationContext applicationContext;

    @RequestMapping("/menus")
    @Remark("显示菜单")
    public Response<List<MenuInfo>> menus() {
        List list= menuProvider.getMenu();
        return respone(list);
    }

    @RequestMapping("/mappings")
    @Remark("显示所有映射")
    public Set mappings(){
        RequestMappingHandlerMapping mapping = applicationContext .getBean(RequestMappingHandlerMapping.class);
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        TreeSet set=new TreeSet();//所有可用的url
        for (RequestMappingInfo info : map.keySet()) {
            Set<String> patterns = info.getPatternsCondition().getPatterns();
            set.addAll(patterns);
        }
        return set;
    }
}
