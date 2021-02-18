package wang.wangby.permission.dao;

import org.apache.ibatis.annotations.Mapper;
import wang.wangby.permission.entity.UserRole;
import wang.wangby.repostory.dao.EntityDao;

@Mapper
public interface UserRoleDao extends EntityDao<UserRole> {
}