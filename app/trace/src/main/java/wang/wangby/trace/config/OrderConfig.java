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

    @Remark("停盈价")
    private BigDecimal step=new BigDecimal(10).setScale(0, RoundingMode.HALF_UP);

    @Remark("最小止损")
    private BigDecimal minStop=new BigDecimal(15).setScale(0, RoundingMode.HALF_UP);


    public BigDecimal getUpgradePrice() {
        return step.multiply(new BigDecimal(2));
    }
}
