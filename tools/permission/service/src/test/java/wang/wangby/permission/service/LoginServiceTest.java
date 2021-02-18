package wang.wangby.permission.service;

import org.junit.Test;
import wang.wangby.base.entity.Entity;
import wang.wangby.permission.entity.*;
import wang.wangby.repostory.EntityDaoFinder;
import wang.wangby.repostory.selector.QueryUtil;
import wang.wangby.test.TestBase;
import wang.wangby.test.TestDao;

import java.util.ArrayList;
import java.util.List;

public class LoginServiceTest extends TestBase {
    public static final Long USER_ID=1L;
    public static final Long ROLE_ID=2L;
    public static final Long MENU_ID=3L;

    @Test
    public void login() {
        TestDao testDao=new TestDao(){
            public List select(Object o) {
                Entity en=(Entity)o;
                log.debug("select:"+o+":"+en.getExt());
                if(o instanceof UserRole){
                    UserRole userRole=new UserRole();
                    userRole.setUserId(USER_ID);
                    userRole.setRoleId(ROLE_ID);
                    List list=new ArrayList();
                    list.add(userRole);
                    return list;
                }
                if(o instanceof Role){
                    Role role=new Role();
                    role.setRoleId(ROLE_ID);
                    List list=new ArrayList();
                    list.add(role);
                    return list;
                }
                if(o instanceof RoleMenu){
                    RoleMenu rm=new RoleMenu();
                    rm.setRoleId(ROLE_ID);
                    rm.setMenuId(MENU_ID);
                    List list=new ArrayList();
                    list.add(rm);
                    return list;
                }
                if(o instanceof Menu){
                    Menu m=(Menu)o;
                    m.setMenuId(MENU_ID);
                    List list=new ArrayList();
                    list.add(m);
                    return list;
                }
                return new ArrayList();
            }
        };
        EntityDaoFinder finder=new EntityDaoFinder(t->testDao);
        QueryUtil queryUtil=new QueryUtil(finder);
        User user=new User();
        user.setUserId(USER_ID);
        queryUtil.fill(user, "userRoleList.role.roleMenuList.menu");
        log.debug(toJson(user));

    }
}