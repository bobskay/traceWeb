package wang.wangby.tools.controller.vo;

import lombok.Data;
import wang.wangby.annotation.Remark;

@Data
public class ShellInfo {
    private String hostname;
    private String command;
    private String username;
    private String password;
    @Remark("同一个页面上次执行的shell标识,需要用这个停止时上次请求")
    private String key;
}
