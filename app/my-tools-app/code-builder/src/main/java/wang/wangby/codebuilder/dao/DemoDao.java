package wang.wangby.codebuilder.dao;

import org.apache.ibatis.annotations.Mapper;
import wang.wangby.codebuilder.model.DemoModel;

import java.util.List;

@Mapper
public interface DemoDao {

    long getCount(DemoModel query);

    List select(DemoModel query);
}
