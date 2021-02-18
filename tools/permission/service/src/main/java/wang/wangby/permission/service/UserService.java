package wang.wangby.permission.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import wang.wangby.permission.dao.RoleDao;
import wang.wangby.permission.dao.UserDao;
import wang.wangby.permission.dao.UserRoleDao;
import wang.wangby.permission.entity.Role;
import wang.wangby.permission.entity.User;
import wang.wangby.permission.entity.UserRole;
import wang.wangby.repostory.service.BaseService;
import wang.wangby.utils.CollectionUtil;
import wang.wangby.utils.IdWorker;

import java.util.List;

@Service
public class UserService extends BaseService<User> {
    public static final String ADMIN="admin";
    public static final String GUEST="guest";


    @Autowired
    UserDao userDao;
    @Autowired
    RoleDao roleDao;
    @Autowired
    UserRoleDao userRoleDao;

    public String encryptPwd(String userName, String pwd) {
        String base = userName + "_" + pwd;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return DigestUtils.md5DigestAsHex(md5.getBytes());
    }

    public void insert(User user) {
        user.setUserId(newId());
        user.setPasswd(encryptPwd(user.getUserName(),user.getPasswd()));
        defaultDao().insert(user);
    }


    public void updateById(User user) {
        user.setPasswd(encryptPwd(user.getUserName(),user.getPasswd()));
        super.updateById(user);
    }

    @Transactional
    public void init() {
        log.info("初始化权限数据");
        User user=new User();
        user.setUserName(ADMIN);
        user.setPasswd(encryptPwd(ADMIN,ADMIN));
        user.setNickName("管理员");
        user.setUserId(IdWorker.nextLong());
        this.insert(user);

        Role admin = getRole(ADMIN,"管理员");
        getRole(GUEST,"游客");


        userRoleDao.delete(user.getUserId());

        UserRole userRole=new UserRole();
        userRole.setUserId(user.getUserId());
        userRole.setPriority(1);
        userRole.setUserRoleId(newId());
        userRole.setRoleId(admin.getRoleId());
        userRoleDao.insert(userRole);
    }


    private Role getRole(String code, String name) {
        Role role=new Role();
        role.setRoleCode(code);
        List<Role> roles=roleDao.select(role);
        if(roles.size()==0){
            role.setRoleId(newId());
            role.setRoleName(name);
            roleDao.insert(role);
        }else{
            role=roles.get(0);
        }
        return role;
    }

    public User getUserRoles(Long userId) {
        User user=this.get(userId);
        queryUtil.fill(user,"userRoles.role");
        List<UserRole> userRoleList=user.get("userRoleList");
        CollectionUtil.sort(userRoleList, t->t.getPriority());
        return user;
    }

    @Transactional
    public void updateUserRole(User user) {
        queryUtil.newSelector(UserRole.class).attr("userId").eq(user.getUserId()).delete();
        List<UserRole> userRoles=user.get("userRoleList");
        CollectionUtil.sort(userRoles, UserRole::getPriority);
        for(int i=0;i<userRoles.size();i++){
            UserRole ur=userRoles.get(i);
            ur.setUserId(user.getUserId());
            ur.setUserRoleId(newId());
            ur.setPriority(i+1);
        }
        if(userRoles.size()==0){
            return;
        }
        userRoleDao.insertBatch(userRoles);
    }
}