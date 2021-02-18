package wang.wangby.permission.dao;

import org.apache.ibatis.annotations.Mapper;
import wang.wangby.permission.entity.Role;
import wang.wangby.repostory.dao.EntityDao;

@Mapper
public interface RoleDao extends EntityDao<Role> {
}
