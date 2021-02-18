package wang.wangby.permission.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.Constants;
import wang.wangby.annotation.Remark;
import wang.wangby.entity.request.Response;
import wang.wangby.permission.entity.User;
import wang.wangby.permission.service.MenuService;
import wang.wangby.permission.service.UserService;
import wang.wangby.web.MenuProvider;

@RestController
@RequestMapping("permission")
public class PermissionController extends PermissionBaseController {


    @Autowired
    UserService userService;
    @Autowired
    MenuService menuService;
    @Autowired
    MenuProvider menuProvider;

    @RequestMapping("/init")
    public String init() {
        return $("init");
    }


    @RequestMapping("/initUsers")
    @Remark("初始化用户数据")
    public Response<String> initUsers() throws Exception {
        User user = new User();
        user.setUserName(Constants.ADMIN);
        User admin = userService.unique(user);
        if (admin != null) {
            return respone("ok");
        }
        userService.init();
        return respone("ok");
    }

    @RequestMapping("/initMenus")
    @Remark("初始化菜单")
    public void initMenus() throws Exception {

    }

}
