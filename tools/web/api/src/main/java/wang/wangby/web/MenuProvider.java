package wang.wangby.web;

import wang.wangby.web.dto.MenuInfo;

import java.util.List;

public interface MenuProvider {

    //生成菜单
    List<MenuInfo> createMenu();

    //获取菜单
    List<MenuInfo> getMenu();

    //controller配置的mapping
    List<MenuInfo> getMappings();

    void reload();
}
