package wang.wangby.trace.config;

import lombok.Data;
import org.springframework.stereotype.Component;
import wang.wangby.annotation.Remark;

@Component
@Data
public class MarketConfig {
    public static String API_KEY;
    public static String SECRET_KEY;
    public static String PASSWORD;

    @Remark("最大持仓")
    private int maxHold = 40;
    @Remark("最小持仓")
    private int base = 15;
    @Remark("卖出价格")
    private int sellPlus = 5;

    @Remark("交易数量")
    private int quantity = 1;

    @Remark("买入价格")
    private int buySubtract = 2;

    @Remark("买入间隔")
    private long buyInterval = 30 * 1000L;

    private String accountSymbol="usdt";



}
