package wang.wangby.exchange.vo;

import lombok.Data;
import wang.wangby.utils.DateTime;

import java.math.BigDecimal;

@Data
public class BalanceVo {
    private BigDecimal total;
    private BigDecimal available;
    private DateTime updated;
    private String symbol;
}
