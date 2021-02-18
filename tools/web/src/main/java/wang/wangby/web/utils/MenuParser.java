package wang.wangby.web.utils;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import wang.wangby.utils.ClassUtil;
import wang.wangby.utils.CollectionUtil;
import wang.wangby.web.controller.BaseController;
import wang.wangby.web.utils.vo.ControllerMenu;
import wang.wangby.web.utils.vo.MethodMenu;
import wang.wangby.web.dto.MenuInfo;

import java.lang.reflect.Method;
import java.util.*;

public class MenuParser {
    List<ControllerMenu> controllerMenus;

    public MenuParser(Collection<BaseController> baseControllers){
        controllerMenus=new ArrayList<>();
        List<BaseController> controllers = new ArrayList<>(baseControllers);
        CollectionUtil.sort(controllers, BaseController::order);
        for(BaseController baseController:controllers){
            if(baseController instanceof ErrorController){
                continue;
            }

            ControllerMenu cm=new ControllerMenu(baseController);
            controllerMenus.add(cm);
            cm.setMethodMenus(new ArrayList<>());
            for(Method method: ClassUtil.getMethodsByAnnotation(baseController.getClass(), RequestMapping.class)){
                MethodMenu methodMenu=new MethodMenu(method,baseController.getPath());
                cm.getMethodMenus().add(methodMenu);
            }
            cm.setPath(baseController.getPath());
        }
    }

    //获得所有映射
    public List<MenuInfo> getMappings(){
        List<MenuInfo> menuInfos=new ArrayList<>();
        Map<String,MenuInfo> dirs=new HashMap<>();
        for(ControllerMenu cm:controllerMenus){
            MenuInfo mm=new MenuInfo();
            mm.setId(cm.getPath());
            mm.setParentId("/");
            mm.setText(cm.getText());

            MenuInfo dir=dirs.get(cm.getText());
            if(dir!=null){
                MethodMenu indexM=cm.getIndexMethod();
                if(indexM==null){
                    indexM=cm.getMethodMenus().get(0);
                }
                mm.setText(indexM.getText());
                mm.setParentId(dir.getId());
                mm.setJson(indexM.isJson());
            }else{
                dirs.put(mm.getText(),mm);
            }
            menuInfos.add(mm);

            for(MethodMenu methodMenu:cm.getMethodMenus()){
                MenuInfo menu=methodMenu.toMenu(mm.getId());
                menuInfos.add(menu);
            }
        }
        return menuInfos;
    }

    //获得配置了Menu注解的菜单信息
    public List<MenuInfo> getMenus() {
        Map<String, MenuInfo> dirs = new HashMap<>();
        List<MenuInfo> returnValue=new ArrayList<>();
        for (ControllerMenu cm : controllerMenus) {
            if(cm.getShowMenus().size()==0){
                continue;
            }
            MenuInfo dir = dirs.get(cm.getText());
            if(dir==null){
                if(cm.getMenu()!=null||cm.getShowMenus().size()>1){
                    dir=new MenuInfo();
                    dir.setParentId("/");
                    dir.setText(cm.getText());
                    dir.setId(cm.getPath());
                    dirs.put(dir.getText(),dir);
                    returnValue.add(dir);
                }else{
                    MethodMenu mm=cm.getShowMenus().get(0);
                    MenuInfo mi=mm.toMenu("/");
                    returnValue.add(mi);
                    continue;
                }
            }
            for(MethodMenu mm:cm.getShowMenus()){
                MenuInfo mi=mm.toMenu(dir.getId());
                returnValue.add(mi);
            }
        }
        return returnValue;
    }

}
