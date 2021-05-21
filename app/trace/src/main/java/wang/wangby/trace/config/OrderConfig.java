package wang.wangby.trace.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wang.wangby.annotation.Remark;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Data
@Slf4j
public class OrderConfig {

    @Remark("每单数量")
    private BigDecimal openQuantity=new BigDecimal(0.1).setScale(2, RoundingMode.HALF_UP);

    @Remark("开仓价格")
    private BigDecimal upgradePrice =new BigDecimal(20).setScale(0, RoundingMode.HALF_UP);

    @Remark("停盈价")
    private BigDecimal step=new BigDecimal(10).setScale(0, RoundingMode.HALF_UP);

    //开仓的买入价格
    public BigDecimal openBuy(BigDecimal price) {
        return price.add(upgradePrice);
    }
}
