package wang.wangby.trace.vo;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.exchange.dto.OpenOrder;

import java.math.BigDecimal;

@Data
public class CurrentOrder {
    @Remark("当前价格")
    private BigDecimal price;
    @Remark("等待买入")
    private OpenOrder buy;
    @Remark("等待卖出")
    private OpenOrder sell;
}
