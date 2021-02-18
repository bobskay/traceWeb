package wang.wangby.permission.dao;


import org.apache.ibatis.annotations.Mapper;
import wang.wangby.permission.entity.Menu;
import wang.wangby.repostory.dao.EntityDao;

@Mapper
public interface MenuDao extends EntityDao<Menu> {

}