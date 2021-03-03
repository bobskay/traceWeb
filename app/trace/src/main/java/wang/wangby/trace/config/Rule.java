package wang.wangby.trace.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.enums.CandlestickInterval;
import wang.wangby.trace.model.Stock;
import wang.wangby.trace.service.KlineService;
import wang.wangby.trace.service.StockService;
import wang.wangby.trace.utils.OrderId;

import java.math.BigDecimal;

@Component
@Slf4j
public class Rule {

    @Autowired
    MarketConfig marketConfig;
    @Autowired
    StockService stockService;
    @Autowired
    KlineService klineService;

    //是否要取消买单
    public boolean isCancel(BigDecimal current, OpenOrder lastBuy) {
        if (current == null) {
            return false;
        }
        return current.intValue()>cancelPrice(current,lastBuy);
    }

    public int cancelPrice(BigDecimal current, OpenOrder lastBuy) {
        if (lastBuy == null) {
            return current.intValue();
        }
        return lastBuy.getPrice().intValue()+marketConfig.getBuyCancel();
    }


    public int buyPrice(BigDecimal currentPrice) {
        OpenOrder o=stockService.getStock().sellPrice();
        //如果一个卖单都没有，说明当前空仓，当前价格减1就买
        if(o==null){
            return currentPrice.intValue()-1;
        }

        return o.getPrice().subtract(marketConfig.getBuySubtract()).intValue();
    }

    public BigDecimal quantity(BigDecimal currentPrice) {
        return marketConfig.getQuantity();
    }

    public BigDecimal sellPrice(BigDecimal currentPrice, BigDecimal quantity) {
        return currentPrice.add(marketConfig.getSellPlus());
    }

    //超过某个值就不下单了
    public BigDecimal stopPrice() {
        BigDecimal price = klineService.getHigh(CandlestickInterval.HALF_HOURLY);
        Double d = price.doubleValue();
        d = d * 0.99;
        return new BigDecimal(d.intValue());
    }


    public BigDecimal totalRemain() {
        Stock stock = stockService.getStock();
        return new BigDecimal(marketConfig.getMaxHold()).subtract(stock.getHolds()).subtract(stock.buyQuantity());
    }
}
