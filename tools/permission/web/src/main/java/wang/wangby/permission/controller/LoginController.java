package wang.wangby.permission.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.Remark;
import wang.wangby.entity.request.Response;
import wang.wangby.permission.config.PermissionProperties;
import wang.wangby.permission.controller.vo.LoginInfo;
import wang.wangby.permission.entity.LoginUser;
import wang.wangby.permission.service.LoginService;
import wang.wangby.utils.StringUtil;
import wang.wangby.web.config.IndexPageProperties;
import wang.wangby.web.controller.BaseController;
import wang.wangby.web.webfilter.permission.CurrentUserHolder;
import wang.wangby.web.webfilter.permission.PermissionUser;
import wang.wangby.web.webfilter.permission.Token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("login")
@RestController
@Remark("登录")
public class LoginController extends BaseController {

    @Autowired
    LoginService loginService;
    @Autowired
    PermissionProperties permissionProperties;
    @Autowired
    IndexPageProperties indexPageProperties;

    @RequestMapping("prepareLogin")
    public String prepareLogin(String forward) {
        Map map = new HashMap<>();
        map.put("forward", forward);
        map.put("title", indexPageProperties.getTitle());
        return $("prepareLogin", map);
    }

    @RequestMapping("logout")
    public String logout(HttpServletResponse response) {
        Map map = new HashMap<>();
        map.put("title", indexPageProperties.getTitle());
        setToken(response, "");
        PermissionUser current = CurrentUserHolder.getUser();
        if (current != null) {
            loginService.logout(CurrentUserHolder.getUser());
        }
        return $("prepareLogin", map);
    }

    @RequestMapping("login")
    public Response<LoginInfo> login(@RequestBody LoginInfo loginInfo, HttpServletResponse response) {
        if (StringUtil.isEmpty(loginInfo.getUsername())) {
            loginInfo.setError("用户名不能为空");
            return respone(loginInfo);
        }
        if (StringUtil.isEmpty(loginInfo.getPassword())) {
            loginInfo.setError("密码不能为空");
            return respone(loginInfo);
        }
        LoginUser user = loginService.login(loginInfo.getUsername(), loginInfo.getPassword());
        if (user == null) {
            loginInfo.setError("用户名或密码错误");
            return respone(loginInfo);
        }
        CurrentUserHolder.setUser(user);
        String tokenId = Token.getTokenId(user.getUser().getUserName(), permissionProperties.getLoginExpireSecond());
        loginInfo.setTokenId(tokenId);
        setToken(response, tokenId);
        return respone(loginInfo);
    }

    private void setToken(HttpServletResponse response, String tokenId) {
        Cookie cookie = new Cookie(Token.COOKIE_NAME, tokenId);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
