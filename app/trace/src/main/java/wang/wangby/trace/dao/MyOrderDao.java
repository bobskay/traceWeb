package wang.wangby.trace.dao;

import org.apache.ibatis.annotations.Mapper;
import wang.wangby.repostory.dao.EntityDao;
import wang.wangby.trace.model.MyOrder;
import wang.wangby.trace.model.Profit;

@Mapper
public interface MyOrderDao extends EntityDao<MyOrder> {
}
