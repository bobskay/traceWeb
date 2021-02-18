package wang.wangby.web.webfilter.permission;

import wang.wangby.web.dto.MenuInfo;

import java.util.List;

//当前用户
public interface PermissionUser {
    //用户名
    String getUsername();

    //是否有访问某个功能的权限
    boolean hasPermission(String taskCoe);

    //获得所有可以访问的菜单
    List<MenuInfo> getMenus();
}
