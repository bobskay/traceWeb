package wang.wangby.trace.dao;

import org.apache.ibatis.annotations.Mapper;
import wang.wangby.repostory.dao.EntityDao;
import wang.wangby.trace.model.Profit;
import wang.wangby.trace.model.StockOrder;

@Mapper
public interface ProfitDao extends EntityDao<Profit> {
}
