package wang.wangby.trace.dao;

import org.apache.ibatis.annotations.Mapper;
import wang.wangby.repostory.dao.EntityDao;
import wang.wangby.trace.model.StockOrder;

@Mapper
public interface StockOrderDao extends EntityDao<StockOrder> {
}
