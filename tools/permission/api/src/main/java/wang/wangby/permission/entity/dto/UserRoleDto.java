package wang.wangby.permission.entity.dto;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;

@Data
@Remark("用户角色")
public class UserRoleDto {
    @Id
    @Remark("主键")
    private Long userRoleId;

    @Remark("角色id")
    private Long roleId;

    @Remark("用户")
    private Long userId;

    @Remark("优先级")
    private Integer priority;
}
