package wang.wangby.permission.entity.dto;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;

@Data
@Remark("角色菜单")
public class RoleMenuDto {

    @Id
    @Remark("主键")
    private Long roleMenuId;

    @Remark("角色id")
    private Long roleId;

    @Remark("菜单id")
    private Long menuId;

    @Remark("权限控制,allow允许,deny禁止")
    private String privilege;

}
