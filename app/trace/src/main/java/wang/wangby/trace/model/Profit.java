package wang.wangby.trace.model;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.base.entity.Entity;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Profit implements Entity {
    @Id
    @Remark("主键")
    private Long id;
    @Remark("主键")
    private Date date;
    @Remark("账户余额")
    private BigDecimal account;
    @Remark("当前价格")
    private BigDecimal price;
    @Remark("成交数据量")
    private int exchangeCount;
    @Remark("成交数据量")
    private BigDecimal exchangeQuantity;
    @Remark("利润总额")
    private BigDecimal profitAmount;
}
