package wang.wangby.permission.entity;


import lombok.Getter;
import lombok.Setter;
import wang.wangby.annotation.persistence.OneToMany;
import wang.wangby.annotation.persistence.OneToOne;
import wang.wangby.base.entity.Entity;
import wang.wangby.permission.entity.dto.UserDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@OneToOne(target = UserDetail.class)
@Getter
@Setter
public class User extends UserDto implements Entity {

    @OneToMany
    private List<UserRole> userRoleList;

    public List<Menu> allowMenus(List<Menu> allMenu) {
        Map<String, Menu> menuMap = new HashMap<>();
        List<UserRole> userRoleList = get("userRoleList");
        for (UserRole ur : userRoleList) {
            Role role = ur.get("role");
            if (role.isAdmin()) {
                return allMenu;
            }
            List<RoleMenu> roleMenus = role.get("roleMenuList");
            for (RoleMenu m : roleMenus) {
                Menu menu = m.get("menu");
                menuMap.put(menu.getMenuCode(), menu);
            }
        }
        return new ArrayList<>(menuMap.values());
    }
}
