package wang.wangby.web.utils.vo;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.web.Menu;
import wang.wangby.utils.ClassUtil;
import wang.wangby.web.controller.BaseController;

import java.util.ArrayList;
import java.util.List;

@Data
public class ControllerMenu {
    private List<MethodMenu> methodMenus;
    private Remark remark;
    private Menu menu;
    private String path;

    public ControllerMenu(BaseController baseController) {
        Class clazz=baseController.getClass();
        remark= ClassUtil.getAnnotation(clazz,Remark.class);
        menu= ClassUtil.getAnnotation(clazz,Menu.class);
        path=baseController.getPath();
    }

    public String getText() {
        if(menu!=null){
            return menu.value();
        }
        if(remark!=null){
            return remark.value();
        }
        return path;
    }

    public MethodMenu getIndexMethod() {
        for(MethodMenu m:methodMenus){
            if(m.isIndex()){
                return m;
            }
        }
        return null;
    }


    public List<MethodMenu> getShowMenus() {
        List list=new ArrayList();
        for(MethodMenu m:methodMenus){
            if(m.getMenu()!=null){
                list.add(m);
            }
        }
        return list;
    }
}
