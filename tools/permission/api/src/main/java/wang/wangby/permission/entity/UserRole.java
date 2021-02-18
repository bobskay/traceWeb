package wang.wangby.permission.entity;

import lombok.Getter;
import lombok.Setter;
import wang.wangby.annotation.persistence.OneToMany;
import wang.wangby.annotation.persistence.OneToOne;
import wang.wangby.base.entity.Entity;
import wang.wangby.permission.entity.dto.UserRoleDto;

@OneToOne(target = Role.class)
@Getter
@Setter
public class UserRole extends UserRoleDto implements Entity {

    @OneToOne
    private Role role;
}
