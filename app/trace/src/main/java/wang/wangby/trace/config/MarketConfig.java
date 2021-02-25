package wang.wangby.trace.config;

import lombok.Data;
import org.springframework.stereotype.Component;
import wang.wangby.annotation.Remark;

import java.math.BigDecimal;

@Component
@Data
public class MarketConfig {
    public static String API_KEY;
    public static String SECRET_KEY;
    public static String PASSWORD;

    @Remark("最大持仓")
    private int maxHold = 10;
    @Remark("最小持仓")
    private int base = 0;
    @Remark("卖出价格")
    private BigDecimal sellPlus = new BigDecimal(5);

    @Remark("交易数量")
    private BigDecimal quantity = new BigDecimal(0.1);

    @Remark("买入价格")
    private BigDecimal buySubtract = new BigDecimal(5);

    @Remark("买入间隔")
    private long buyInterval = 15 * 1000L;

    private String accountSymbol="usdt";



}
