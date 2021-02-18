package wang.wangby.web.utils.vo;

import lombok.Data;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.web.Menu;
import wang.wangby.web.dto.MenuInfo;

import java.lang.reflect.Method;

@Data
public class MethodMenu {
    private RequestMapping requestMapping;
    private Menu menu;
    private Remark remark;
    private String methodName;
    private String path;
    private Method method;

    public MethodMenu(Method method,String path){
        requestMapping= AnnotationUtils.getAnnotation(method, RequestMapping.class);
        menu= AnnotationUtils.getAnnotation(method, Menu.class);
        remark= AnnotationUtils.getAnnotation(method, Remark.class);
        methodName=method.getName();
        this.method=method;
        this.path=path;
    }

    public String getText() {
        if(menu!=null){
            return menu.value();
        }
        if(remark!=null){
            return remark.value();
        }
        return methodName;
    }

    public String getUrl() {
        String mapping= requestMapping.value()[0];
        if(!mapping.startsWith("/")){
            mapping="/"+mapping;
        }
        return path+mapping;
    }


    public MenuInfo toMenu(String parentId){
        MenuInfo menu=new MenuInfo();
        menu.setId(this.getUrl());
        menu.setParentId(parentId);
        menu.setText(this.getText());
        menu.setUrl(this.getUrl());
        menu.setJson(this.isJson());
        return menu;
    }

    public boolean isJson(){
        return method.getReturnType()!=String.class;
    }

    public boolean isIndex() {
        return "index".equalsIgnoreCase(methodName);
    }
}
