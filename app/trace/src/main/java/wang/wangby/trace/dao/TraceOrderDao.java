package wang.wangby.trace.dao;

import org.apache.ibatis.annotations.Mapper;
import wang.wangby.repostory.dao.EntityDao;
import wang.wangby.trace.model.StockOrder;
import wang.wangby.trace.model.TraceOrder;

@Mapper
public interface TraceOrderDao extends EntityDao<TraceOrder> {
}
