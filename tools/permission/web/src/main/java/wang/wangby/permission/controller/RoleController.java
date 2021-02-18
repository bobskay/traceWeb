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
import wang.wangby.permission.entity.RoleMenu;
import wang.wangby.permission.service.MenuService;
import wang.wangby.permission.service.RoleService;
import wang.wangby.utils.CollectionUtil;
import wang.wangby.web.dto.Ztree;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("role")
public class RoleController extends PermissionBaseController {

    @Autowired
    RoleService roleService;
    @Autowired
    MenuService menuService;

    @RequestMapping("/index")
    @Menu("角色管理")
    public String index() {
        List<Role> roles = roleService.getAll();
        return $("index", roles);
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
    public Response<Role> insert(Role role) throws IOException {
        roleService.insert(role);
        return respone(role);
    }

    @RequestMapping("/select")
    @Remark("查询图书")
    @Param("查询条件")
    @Param("起始条数,从0开始")
    @Param("返回条数")
    @Return("分页后的查询结果")
    public Response<Pagination> select(Role role, Integer offset, Integer limit) {
        Pagination page = roleService.selectPage(role, offset, limit);
        return respone(page);
    }

    @Remark("通过主键删除")
    @RequestMapping("/deleteById")
    @Param("要删除的数据Id")
    @Return("删除记录数")
    public Response<Integer> deleteById(Long roleId) {
        return respone(roleService.deleteById(roleId));
    }

    @Remark("通过主键删除图书")
    @RequestMapping("/updateById")
    public Response<Role> updateById(Role role) {
        roleService.updateById(role);
        return respone(role);
    }


    @Remark("进入修改页面")
    @RequestMapping("/prepareUpdate")
    @Param("主键")
    public String prepareUpdate(Long roleId) {
        if (roleId == null) {
            throw new Message("主键不能为空");
        }
        Role model = roleService.get(roleId);
        return $("prepareUpdate", model);
    }

    @Remark("获取角色所有菜单")
    @RequestMapping("/roleMenus")
    @Param("主键")
    public String roleMenus(Long roleId) {
        Map map = new HashMap<>();
        map.put("roleId", roleId);
        return $("roleMenus", map);
    }

    @Remark("获取角色所有菜单")
    @RequestMapping("/menuTree")
    @Param("主键")
    public Response<List<Ztree>> menuTree(Long roleId) {
        List<wang.wangby.permission.entity.Menu> allMenu = menuService.getAll();
        Role role = roleService.getRoleMenus(roleId);
        List<RoleMenu> roleMenuList=role.get("roleMenuList");
        Map<Long, RoleMenu> menuMap = CollectionUtil.toMap(roleMenuList, RoleMenu::getMenuId);
        List result = Ztree.createTree(allMenu, m -> {
            Ztree ztree = new Ztree();
            ztree.setId(m.getMenuId()+"");
            ztree.setName(m.getMenuText());
            ztree.setChecked(menuMap.get(m.getMenuId()) != null);
            ztree.setParentId("0");
            for(wang.wangby.permission.entity.Menu am:allMenu){
                if(am.getMenuCode().equalsIgnoreCase(m.getParentMenu())){
                    ztree.setParentId(am.getMenuId()+"");
                    break;
                }
            }
            return ztree;
        });
        return respone(result);
    }

    @RequestMapping("updateRoleMenu")
    @Remark("修改角色菜单")
    public Response<String> updateRoleMenu(@RequestBody Role role){
        assertNotNull(role.getRoleId(),"角色id不能为空");
        roleService.updateRoleMenu(role);
        return respone("ok");
    }
}
