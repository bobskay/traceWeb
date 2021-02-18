package wang.wangby.repository.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GeneralDao {

    List<Map> select(Map sql);
}
