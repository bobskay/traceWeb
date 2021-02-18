package wang.wangby.permission.controller.vo;

import lombok.Data;

@Data
public class LoginInfo {
    private String username;
    private String password;
    private boolean remeber;
    private String tokenId;
    private String error;
}
