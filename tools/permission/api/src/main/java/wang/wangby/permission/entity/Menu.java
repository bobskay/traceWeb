package wang.wangby.permission.entity;


import wang.wangby.base.entity.Entity;
import wang.wangby.permission.entity.dto.MenuDto;
import wang.wangby.web.dto.MenuInfo;

public class Menu extends MenuDto implements Entity {

    //通过controller生成的MenuInfo对象生成menu
    public static Menu createByController(MenuInfo info, int idx) {
        Menu menu=new Menu();
        menu.setMenuIndex(idx);
        menu.setMenuCode(info.getId());
        menu.setParentMenu(info.getParentId());
        menu.setMenuText(info.getText());
        return menu;
    }
}
