package wang.wangby.tools.controller.vo;

import lombok.Data;

@Data
public class AceTip {
    private String name;
    private String value;//实际输入值
    private String caption;//显示名称
    private String meta;//提示类型
    private String type="local";
    private Integer score=100;//自定义提示显示在最上面

}
