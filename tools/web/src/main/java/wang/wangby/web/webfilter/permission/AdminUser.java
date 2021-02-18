package wang.wangby.web.webfilter.permission;

import wang.wangby.Constants;
import wang.wangby.web.dto.MenuInfo;

import java.util.ArrayList;
import java.util.List;

public class AdminUser implements PermissionUser{
    @Override
    public String getUsername() {
        return Constants.ADMIN;
    }

    @Override
    public boolean hasPermission(String taskCoe) {
        return true;
    }

    @Override
    public List<MenuInfo> getMenus() {
        return new ArrayList<>();
    }
}
