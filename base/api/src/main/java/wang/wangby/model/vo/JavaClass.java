package wang.wangby.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class JavaClass {
    private List<JavaField> javaFieldList;
    private String packageName;
    private List<String> importList;
    private String name;
    private JavaField pkField;
    //最终生成的文件内容
    private String content;
}
