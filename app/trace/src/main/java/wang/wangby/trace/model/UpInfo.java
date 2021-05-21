package wang.wangby.trace.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpInfo {
    private BigDecimal upgradePrice;
    private BigDecimal reOrderPrice;
    private boolean up;
    private BigDecimal diff;
}
