package wang.wangby.trace.config;

import cn.hutool.core.io.FileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wang.wangby.annotation.Remark;

import java.io.File;
import java.math.BigDecimal;

@Component
@Data
@Slf4j
public class MarketConfig {
    public static String API_KEY;
    public static String SECRET_KEY;
    public static String PASSWORD;
    public static boolean test=false;

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
        if(keys.length>3){
            try{
                test=Boolean.parseBoolean(keys[3].trim());
            }catch (Exception ex){
                log.error(ex.getMessage(),ex);
            }
        }
    }

    @Remark("最大持仓")
    private BigDecimal maxHold = new BigDecimal(10);
    @Remark("最小持仓")
    private int base = 0;
    @Remark("买入后加价卖出")
    private BigDecimal sellPlus = new BigDecimal(100);

    @Remark("交易数量")
    private BigDecimal quantity = new BigDecimal("00.1").setScale(2);

    @Remark("最后一个卖价减去这个价格，挂买单")
    private BigDecimal buySubtract = new BigDecimal(400);

    @Remark("买入间隔")
    private long buyInterval = 20 * 1000L;

    private String accountSymbol="usdt";

    @Remark("当前价格过买价多少后重新申请")
    public int buyCancel=500;

    @Remark("持仓小于多少就忽略当前买入数量")
    public BigDecimal ignoreMin=new BigDecimal(0);

    @Remark("小于这个持仓时的买入")
    public BigDecimal minQuantity=new BigDecimal("00.1");
}
