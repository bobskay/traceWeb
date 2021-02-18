package wang.wangby.permission.entity.dto;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.annotation.persistence.Length;

@Data
@Remark("角色信息")
public class RoleDto {
    @Id
    @Remark("角色id")
    private Long roleId;

    @Length(255)
    @Remark("角色代码")
    private String roleCode;

    @Length(128)
    @Remark("角色名称")
    private String roleName;

}
