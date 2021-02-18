package wang.wangby.permission.entity.dto;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.annotation.persistence.Table;
import wang.wangby.enums.Gender;

import java.util.Date;

@Data
@Remark("用户信息")
@Table("t_userDetail")
public class UserDetailDto {
    @Id
    @Remark("用户id")
    private Long userId;

    @Remark("性别")
    private Gender gender;

    @Remark("出生日期")
    private Date birthday;
}
