package wang.wangby.permission.entity;

import lombok.Data;
import wang.wangby.Constants;
import wang.wangby.annotation.Remark;
import wang.wangby.web.dto.MenuInfo;
import wang.wangby.web.webfilter.permission.PermissionUser;

import java.util.List;

@Data
@Remark("登录后的用户信息")
public class LoginUser implements PermissionUser {

    private User user;
    private List<Menu> avaiMenus;
    @Remark("显示用的菜单,需要组装成树")
    private List<MenuInfo> displayMenus;


    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean hasPermission(String taskCode) {
        if(this.isAdmin()){
            return true;
        }
        for(Menu m:avaiMenus){
            if(m.getMenuCode().equalsIgnoreCase(taskCode)){
                return true;
            }
        }
        return false;
    }

    private boolean isAdmin(){
        List<UserRole> userRoleList=user.get("userRoleList");
        for(UserRole role:userRoleList){
            Role r=role.get("role");
            if(Constants.ADMIN.equalsIgnoreCase(r.getRoleCode())){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<MenuInfo> getMenus() {
        return displayMenus;
    }

}
