package wang.wangby.permission.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wang.wangby.Constants;
import wang.wangby.exception.Message;
import wang.wangby.permission.dao.RoleDao;
import wang.wangby.permission.dao.RoleMenuDao;
import wang.wangby.permission.dao.UserRoleDao;
import wang.wangby.permission.entity.Role;
import wang.wangby.permission.entity.RoleMenu;
import wang.wangby.permission.entity.UserRole;
import wang.wangby.permission.entity.enums.Privilege;
import wang.wangby.repostory.service.BaseService;
import wang.wangby.utils.CollectionUtil;

import java.util.List;

@Service
public class RoleService extends BaseService<Role> {
    @Autowired
    RoleDao roleDao;
    @Autowired
    UserRoleDao userRoleDao;
    @Autowired
    RoleMenuDao roleMenuDao;


    public List<Role> getByUser(Long userId) {
        List<UserRole> userRoles = queryUtil.newSelector(UserRole.class).attr("userId").eq(userId).list();
        List<Long> roleIds = CollectionUtil.pickUp(userRoles, UserRole::getRoleId);
        List<Role> roles =get(roleIds);
        //将role填充到userRole
        CollectionUtil.set(userRoles, roles, (bean,v)->bean.set("role",v), (a, b) -> {
            return a.getRoleId().longValue() == b.getRoleId().longValue();
        });
        //将userRole按优先级排序
        CollectionUtil.sort(userRoles, UserRole::getPriority);
        List<Role> result = CollectionUtil.pickUp(userRoles,r->r.get("role"));
        return result;
    }

    public Role getRoleMenus(Long roleId) {
        Role role=this.get(roleId);
        queryUtil.fill(role,"roleMenus.menu");
        return role;
    }

    @Transactional
    public Integer deleteById(Long roleId) {
        Role role=this.get(roleId);
        long i=queryUtil.newSelector(UserRole.class).attr("roleId").eq(roleId).count();
        if(i>0){
            throw new Message("当前角色有{0}个用户在使用{1},无法删除",i,role.getRoleName());
        }
        if(role.getRoleCode().equalsIgnoreCase(Constants.ADMIN)){
            throw new Message("无法删除管理员");
        }
        queryUtil.newSelector(RoleMenu.class).attr("roleId").eq(roleId).delete();
        return queryUtil.newSelector(Role.class).attr("roleId").eq(roleId).delete();
    }

    @Transactional
    public void updateRoleMenu(Role role) {
        queryUtil.newSelector(RoleMenu.class).attr("roleId").eq(role.getRoleId()).delete();
        List<RoleMenu> roleMenuList=role.get("roleMenuList");
        for(int i=0;i<roleMenuList.size();i++){
            RoleMenu rm=roleMenuList.get(i);
            rm.setRoleMenuId(newId());
            rm.setRoleId(role.getRoleId());
            rm.setPrivilege(Privilege.allow+"");
        }
        if(roleMenuList.size()==0){
            return;
        }
        roleMenuDao.insertBatch(roleMenuList);
    }
}
