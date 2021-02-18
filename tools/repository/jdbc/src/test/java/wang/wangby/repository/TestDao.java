package wang.wangby.repository;

import org.apache.ibatis.annotations.Mapper;
import wang.wangby.permission.entity.User;
import wang.wangby.repostory.dao.EntityDao;

@Mapper
public interface TestDao extends EntityDao<User> {
}
