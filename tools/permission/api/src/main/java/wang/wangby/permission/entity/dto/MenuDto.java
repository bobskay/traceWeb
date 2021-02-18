package wang.wangby.permission.entity.dto;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.annotation.persistence.Length;

@Data
@Remark("菜单")
public class MenuDto {
    @Id
    @Remark("菜单id")
    private Long menuId;

    @Length(255)
    @Remark("菜单代码，就是requestMapping")
    private String menuCode;

    @Length(255)
    @Remark("显示图标")
    private String menuIcon;

    @Length(128)
    @Remark("显示名称")
    private String menuText;

    @Length(255)
    @Remark("搜索菜单时用到")
    private String keyword;

    @Remark("显示位置")
    private Integer menuIndex;

    @Remark("上级菜单")
    private String parentMenu;

}