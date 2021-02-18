package wang.wangby.permission.entity.dto;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.annotation.persistence.Length;
import wang.wangby.annotation.persistence.Table;

@Data
@Remark("用户信息")
@Table("t_user")
public class UserDto{
    @Id
    @Remark("用户id")
    private Long userId;

    @Length(64)
    @Remark("用户名")
    private String userName;

    @Length(128)
    @Remark("密码")
    private String passwd;

    @Length(64)
    @Remark("昵称")
    private String nickName;

}