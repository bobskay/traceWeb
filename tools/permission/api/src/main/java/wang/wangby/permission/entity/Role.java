package wang.wangby.permission.entity;


import lombok.Getter;
import lombok.Setter;
import wang.wangby.Constants;
import wang.wangby.annotation.persistence.OneToMany;
import wang.wangby.base.entity.Entity;
import wang.wangby.permission.entity.dto.RoleDto;

import java.util.List;

@OneToMany(target = UserRole.class)
@Getter
@Setter
public class Role extends RoleDto implements Entity {

    @OneToMany
    private List<RoleMenu> roleMenuList;

    public boolean isAdmin() {
        return Constants.ADMIN.equals(this.getRoleCode());
    }
}
