package wang.wangby.permission.dao;

import org.apache.ibatis.annotations.Mapper;
import wang.wangby.permission.entity.User;
import wang.wangby.repostory.dao.EntityDao;

@Mapper
public interface UserDao extends EntityDao<User> {
}