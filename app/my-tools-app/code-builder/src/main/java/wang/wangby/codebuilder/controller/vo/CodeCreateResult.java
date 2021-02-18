package wang.wangby.codebuilder.controller.vo;

import lombok.Data;
import wang.wangby.model.vo.JavaClass;
import wang.wangby.repostory.database.dto.TableInfo;
import wang.wangby.utils.StringUtil;

import java.util.List;

@Data
public class CodeCreateResult {
    private List<JavaClass> javaClasses;
    private List<PageInfo> pages;
    private String modelName;
    private TableInfo tableInfo;
    private String pkName;

    public JavaClass model() {
        for (JavaClass javaClass : javaClasses) {
            if (javaClass.getName().equalsIgnoreCase(modelName)) {
                return javaClass;
            }
        }
        return null;
    }

    public JavaClass service() {
        return getClass("Service");
    }

    public JavaClass dao() {
        return getClass("Dao");
    }

    public JavaClass controller() {
        return getClass("Controller");
    }

    public JavaClass getClass(String tail) {
        for (JavaClass javaClass : javaClasses) {
            if (javaClass.getName().endsWith(tail)) {
                return javaClass;
            }
        }
        return null;
    }

    //首字母大写的modelName
    public String ModelName() {
        return StringUtil.firstUp(modelName);
    }

    public String getUpPkName(){
        return StringUtil.firstUp(pkName);
    }
}
