package wang.wangby.trace.model;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.base.entity.Entity;
import wang.wangby.exchange.enums.OrderSide;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StockOrder implements Entity {

    @Id
    @Remark("主键")
    private Long id;
    @Remark("订单id")
    private String clientOrderId;
    @Remark("价格")
    private BigDecimal price;
    @Remark("订单类型")
    private String type;
    @Remark("交易数量")
    private BigDecimal quantity;
    @Remark("时间")
    private Date createdAt;
    @Remark("原始信息")
    private String ori;
    private BigDecimal finish;

    public OrderSide getSide() {
        return OrderSide.getById(clientOrderId);
    }


}
