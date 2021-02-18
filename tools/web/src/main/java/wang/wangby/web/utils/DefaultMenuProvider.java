package wang.wangby.web.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import wang.wangby.web.MenuProvider;
import wang.wangby.web.config.IndexPageProperties;
import wang.wangby.web.controller.BaseController;
import wang.wangby.web.dto.MenuInfo;

import java.util.List;
import java.util.Map;

@Slf4j
public class DefaultMenuProvider implements MenuProvider{

    volatile List<MenuInfo> menus;
    @Autowired
    IndexPageProperties pageProperties;
    @Autowired
    ApplicationContext applicationContext;

    //通过controller方法上的menu标签,创建显示用的菜单
    public List<MenuInfo> getMenu() {
        if (menus != null) {
            return menus;
        }
        synchronized (MenuProvider.class) {
            if (menus != null) {
                return menus;
            }
            menus = createMenu();
            return menus;
        }
    }

    public List<MenuInfo> getMappings() {
        Map map = applicationContext.getBeansOfType(BaseController.class);
        MenuParser menuParser=new MenuParser(map.values());
        return menuParser.getMappings();

    }

    //生成菜单,通过覆盖这个方法程序可以自定义菜单生成方式
    public List<MenuInfo> createMenu() {
        Map map = applicationContext.getBeansOfType(BaseController.class);
        MenuParser menuParser=new MenuParser(map.values());
        List<MenuInfo> allMenu = menuParser.getMenus();
        if (log.isDebugEnabled()) {
            log.debug("系统菜单个数{}", allMenu.size());
        }
        return allMenu;
    }

    //刷新菜单
    public void reload(){
        menus = createMenu();
    }
}
