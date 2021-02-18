package wang.wangby.web.controller;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.Remark;
import wang.wangby.utils.StringUtil;
import wang.wangby.utils.tree.TreeUtil;
import wang.wangby.web.MenuProvider;
import wang.wangby.web.config.IndexPageProperties;
import wang.wangby.web.config.MyFilterProperties;
import wang.wangby.web.dto.MenuInfo;
import wang.wangby.web.webfilter.permission.CurrentUserHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Remark("首页")
public class IndexController extends BaseController implements ApplicationContextAware {
    ApplicationContext applicationContext;
    @Autowired
    IndexPageProperties indexPageProperties;
    @Autowired
    MenuProvider menuProvider;
    @Autowired
    MyFilterProperties myFilterProperties;

    @RequestMapping("/")
    @Remark("首页")
    public String index(String forward) {
        Map map=new HashMap();
        map.put("title",indexPageProperties.getTitle());
        if(StringUtil.isEmpty(forward)){
            forward=indexPageProperties.getIndexContentUrl();
        }
        map.put("forward", forward);
        List<MenuInfo> menus;
        if(CurrentUserHolder.getUser()!=null){
            menus= CurrentUserHolder.getUser().getMenus();
        }else{
            menus=menuProvider.getMenu();
            menus= TreeUtil.createTree(menus, t->t);
        }
        map.put("menus",menus);
        map.put("indexPageProperties",indexPageProperties);
        map.put("user",CurrentUserHolder.getUser());
        return $("/index",map);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
