package wang.wangby.permission.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.api.Param;
import wang.wangby.annotation.api.Return;
import wang.wangby.annotation.web.Menu;
import wang.wangby.entity.Pagination;
import wang.wangby.entity.request.Response;
import wang.wangby.exception.Message;
import wang.wangby.permission.entity.Role;
import wang.wangby.permission.entity.User;
import wang.wangby.permission.entity.UserRole;
import wang.wangby.permission.service.RoleService;
import wang.wangby.permission.service.UserService;
import wang.wangby.utils.CollectionUtil;
import wang.wangby.web.controller.BaseController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
@Menu("权限管理")
public class UserController extends BaseController {

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    @RequestMapping("/index")
    @Menu("用户管理")
    public String index() {
        return $("index");
    }

    @RequestMapping("/prepareInsert")
    @Remark("进入新增页面")
    public String prepareInsert() {
        return $("prepareInsert");
    }


   @RequestMapping("/insert")
   @Remark("新增数据")
   @Param("要插入数据库的对象")
   @Return("新增成功后的数据,填充了主键")
   public Response<User> insert(User user) {
       userService.insert(user);
       return respone(user);
   }

    @RequestMapping("/select")
    @Remark("查询")
    @Param("查询条件")
    @Param("起始条数,从0开始")
    @Param("返回条数")
    @Return("分页后的查询结果")
    public Response<Pagination> select(User user, Integer offset, Integer limit) {
        Pagination page = userService.selectPage(user, offset, limit);
        return respone(page);
    }

    @Remark("通过主键删除")
    @RequestMapping("/deleteById")
    @Param("要删除的数据Id")
    @Return("删除记录数")
    public Response<Integer> deleteById(Long[] userId) {
        return respone(userService.deleteById(userId));
    }

    @Remark("通过主键删除数据")
    @RequestMapping("/updateById")
    public Response<User> updateById(User user) {
        userService.updateById(user);
        return respone(user);
    }


    @Remark("进入修改页面")
    @RequestMapping("/prepareUpdate")
    @Param("主键")
    public  String prepareUpdate(Long userId) {
        if (userId == null) {
            throw new Message("主键不能为空");
        }
        User model = userService.get(userId);
        return $("prepareUpdate",model);
    }

    @RequestMapping("prepareUpdateUserRole")
    @Remark("查看用户角色")
    public String prepareUpdateUserRole(Long userId){
        assertNotNull(userId,"用id不能为空");
        List<Role> roleList=roleService.getAll();
        Map<String, UserRole> urMap=new HashMap<>();
        roleList.forEach(r->{
            UserRole ur=new UserRole();
            ur.set("role",r);
            ur.setRoleId(r.getRoleId());
            urMap.put(r.getRoleCode(),ur);
        });

        User user=userService.getUserRoles(userId);
        List<UserRole> userRoleList=user.get("userRoleList");
        userRoleList.forEach(ur->{
            Role r=ur.get("role");
            urMap.put(r.getRoleCode(),ur);
        });
        List<UserRole> list=new ArrayList(urMap.values());
        Map map=new HashMap();
        map.put("userRoleList", CollectionUtil.sort(list, UserRole::getPriority));
        map.put("user",user);
        return $("prepareUpdateUserRole",map);
    }

    @RequestMapping("updateUserRole")
    @Remark("修改用户角色")
    public Response<String> updateUserRole(@RequestBody User user){
        assertNotNull(user.getUserId(),"用id不能为空");
        userService.updateUserRole(user);
        return respone("ok");
    }

}