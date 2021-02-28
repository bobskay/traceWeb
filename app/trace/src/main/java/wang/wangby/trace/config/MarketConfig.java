package wang.wangby.trace.config;

import cn.hutool.core.io.FileUtil;
import lombok.Data;
import org.springframework.stereotype.Component;
import wang.wangby.annotation.Remark;

import java.io.File;
import java.math.BigDecimal;

@Component
@Data
public class MarketConfig {
    public static String API_KEY;
    public static String SECRET_KEY;
    public static String PASSWORD;

    static {
        File file=new File("/opt/config/traceConfig");
        if(!file.exists()){
            throw new RuntimeException("找不到配置文件："+file.getAbsolutePath());
        }
        String text= FileUtil.readString(file,"UTF-8");
        String[] keys=text.trim().split("\n");
        MarketConfig.API_KEY=keys[0].trim();
        MarketConfig.SECRET_KEY=keys[1].trim();
        MarketConfig.PASSWORD=keys[2].trim();
    }

    @Remark("最大持仓")
    private int maxHold = 10;
    @Remark("最小持仓")
    private int base = 0;
    @Remark("卖出价格")
    private BigDecimal sellPlus = new BigDecimal(5);

    @Remark("交易数量")
    private BigDecimal quantity = new BigDecimal("0.2");

    @Remark("买入价格")
    private BigDecimal buySubtract = new BigDecimal(5);

    @Remark("买入间隔")
    private long buyInterval = 15 * 1000L;

    private String accountSymbol="usdt";



}
