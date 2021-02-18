package wang.wangby.permission.dao;

import org.apache.ibatis.annotations.Mapper;
import wang.wangby.permission.entity.RoleMenu;
import wang.wangby.repostory.dao.EntityDao;

@Mapper
public interface RoleMenuDao extends EntityDao<RoleMenu> {
}