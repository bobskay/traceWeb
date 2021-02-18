package wang.wangby.permission.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.Remark;
import wang.wangby.entity.request.Response;
import wang.wangby.permission.entity.Menu;
import wang.wangby.permission.service.MenuService;
import wang.wangby.utils.tree.TreeUtil;
import wang.wangby.web.MenuProvider;
import wang.wangby.web.dto.MenuInfo;
import wang.wangby.web.dto.Ztree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("menu")
public class MenuController extends PermissionBaseController {

    @Autowired
    MenuProvider menuProvider;
    @Autowired
    MenuService menuService;

    @RequestMapping("/index")
    @wang.wangby.annotation.web.Menu("菜单管理")
    public String index() {
        return $("index");
    }

    @RequestMapping("/tree")
    @ResponseBody
    @Remark("获得菜单树")
    public Response<List<Ztree>> tree() {
        List<MenuInfo>  menuInfos=  menuProvider.getMenu();
        List<Menu> menus=menuService.getAll();
        //最终可访问的菜单
        List<MenuInfo> avaiMenus=new ArrayList<>();
        menuInfos.forEach(info -> {
            for (Menu m : menus) {
                if (m.getMenuCode().equalsIgnoreCase(info.getId())) {
                    MenuInfo mm = new MenuInfo();
                    mm.setParentId(info.getParentId());
                    mm.setIndex(m.getMenuIndex());
                    mm.setText(m.getMenuText());
                    mm.setId(info.getId());
                    mm.setChildren(new ArrayList());
                    mm.setUrl(info.getUrl());
                    avaiMenus.add(mm);
                    return;
                }
            }
        });

        List<Ztree> list = Ztree.createTree(avaiMenus, this::menuToZtree);
        return respone(list);
    }

    private Ztree menuToZtree(MenuInfo node){
        Ztree view = new Ztree();
        view.setParentId(node.getParentId());
        view.setId(node.getId());
        view.setName(node.getText());
        view.setIndex(node.getIndex());
        return view;
    }


    @RequestMapping("/icon")
    @Remark("显示图标")
    public String icon(){
        return $("icon");
    }

    @RequestMapping("/update")
    @Remark("更新菜单")
    public Response<Menu> update(@RequestBody Menu menu ){
        menuService.updateMenu(menu);
       return respone(menu);
    }

    @RequestMapping("/get")
    public String get(String menuCode) throws IOException {
        Menu menu=menuService.getMenuByCode(menuCode);
        if(menu==null){
            log.debug("发现新菜单:{}",menuCode);
            Menu newMenu=new Menu();
            List<MenuInfo>  menuInfos=  menuProvider.getMenu();
            TreeUtil.iteratorIndex(menuInfos,(info, idx)->{
                if(info.getId().equalsIgnoreCase(menuCode)){
                    Menu m = Menu.createByController(info, idx);
                    BeanUtils.copyProperties(m,newMenu);
                    return false;
                }
                return true;
            });
            menuService.insert(newMenu);
            menu=newMenu;
        }
        return $("get",menu);
    }
}