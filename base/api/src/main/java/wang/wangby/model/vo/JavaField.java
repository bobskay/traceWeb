package wang.wangby.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class JavaField {
    private String name;
    private String typeName;
    private List<String> annotation;
    private String remark;
}
