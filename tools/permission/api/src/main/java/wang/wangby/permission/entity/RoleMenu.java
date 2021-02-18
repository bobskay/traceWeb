package wang.wangby.permission.entity;

import lombok.Getter;
import lombok.Setter;
import wang.wangby.annotation.persistence.OneToOne;
import wang.wangby.base.entity.Entity;
import wang.wangby.permission.entity.dto.RoleMenuDto;

@Getter
@Setter
public class RoleMenu extends RoleMenuDto implements Entity {

    @OneToOne
    private Menu menu;

}
