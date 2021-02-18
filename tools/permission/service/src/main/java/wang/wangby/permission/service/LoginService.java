package wang.wangby.permission.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.wangby.permission.dao.UserRoleDao;
import wang.wangby.permission.entity.LoginUser;
import wang.wangby.permission.entity.Menu;
import wang.wangby.permission.entity.User;
import wang.wangby.repostory.selector.QueryUtil;
import wang.wangby.tools.cache.Cache;
import wang.wangby.utils.StringUtil;
import wang.wangby.utils.tree.TreeUtil;
import wang.wangby.web.MenuProvider;
import wang.wangby.web.dto.MenuInfo;
import wang.wangby.web.webfilter.permission.PermissionUser;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LoginService {

    @Autowired
    Cache<String, LoginUser> loginUserCache;
    @Autowired
    UserRoleDao userRoleDao;
    @Autowired
    RoleService roleService;

    @Autowired
    UserService userService;
    @Autowired
    MenuService menuService;
    @Autowired
    MenuProvider menuProvider;
    @Autowired
    QueryUtil queryUtil;

    public LoginUser login(String username, String password) {
        User user = new User();
        user.setUserName(username);
        User db = userService.unique(user);
        if (db == null) {
            return null;
        }
        if (userService.encryptPwd(db.getUserName(), password).equalsIgnoreCase(db.getPasswd())) {
            return getLoginUser(db.getUserName());
        }
        return null;
    }

    public LoginUser getLoginUser(String userName) {
        return Cache.get(loginUserCache,userName,null, this::getLoginUserInner);
    }

    private LoginUser getLoginUserInner(String userName) {
        User user = queryUtil.newSelector(User.class).attr("userName").eq(userName).get();
        queryUtil.fill(user, "userRoleList.role.roleMenuList.menu");

        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);

        List<MenuInfo> menuInfos = menuProvider.getMenu();
        List<Menu> allMenu=menuService.getAllMenu();

        List<Menu> allow = user.allowMenus(allMenu);

        //最终可访问的菜单
        List<MenuInfo> avaiMenus=new ArrayList<>();
        menuInfos.forEach(info -> {
            for (Menu m : allow) {
                if (m.getMenuCode().equalsIgnoreCase(info.getId())) {
                    MenuInfo mm = new MenuInfo();
                    mm.setParentId(info.getParentId());
                    mm.setIndex(m.getMenuIndex());
                    mm.setIcon(m.getMenuIcon());
                    mm.setText(m.getMenuText());
                    mm.setId(info.getId());
                    mm.setChildren(new ArrayList());
                    mm.setUrl(info.getUrl());
                    if (StringUtil.isEmpty(mm.getIcon())) {
                        if (StringUtil.isEmpty(mm.getUrl())) {
                            mm.setIcon("fa fa-fw fa-th-list");
                        } else {
                            mm.setIcon("fa fa-circle-o text-aqua");
                        }
                    }
                    avaiMenus.add(mm);
                    return;
                }
            }
        });

        loginUser.setDisplayMenus(TreeUtil.createTree(avaiMenus, t -> t));
        loginUser.setAvaiMenus(allow);
        return loginUser;
    }

    public void logout(PermissionUser user) {
        loginUserCache.delete(user.getUsername());
    }
}
