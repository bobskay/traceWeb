package wang.wangby.trace.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.enums.CandlestickInterval;
import wang.wangby.trace.model.Stock;
import wang.wangby.trace.service.KlineService;
import wang.wangby.trace.service.StockService;

import java.beans.BeanInfo;
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
        return current.intValue() > cancelPrice(lastBuy);
    }

    public int cancelPrice(OpenOrder lastBuy) {
        Stock stock = stockService.getStock();
        int hasBuy = stock.getHolds() - marketConfig.getBase();
        //当前价格>上次下单价+买加价格+已买/2
        int maxCancel = lastBuy.getPrice().intValue() + marketConfig.getBuyMinutes() + hasBuy / 2;
        return maxCancel;
    }


    public BigDecimal buyPrice(BigDecimal currentPrice) {
        Stock stock = stockService.getStock();
        int hasBuy = stock.getHolds() - marketConfig.getBase();
        //买入价格=当前价格-买入差价-已买/3
        int price = currentPrice.intValue() - marketConfig.getBuyMinutes() - hasBuy / 4;
        return new BigDecimal(price);
    }

    public BigDecimal quantity(BigDecimal currentPrice) {
        Stock stock = stockService.getStock();
        int hasBuy = stock.getHolds() - marketConfig.getBase();
        if (hasBuy > 5 && hasBuy < 10) {
            return new BigDecimal(2);
        }
        return new BigDecimal(1);
    }

    public BigDecimal sellPrice(BigDecimal currentPrice) {
        int sell = currentPrice.intValue() + marketConfig.getSellPlus();
        return new BigDecimal(sell);
    }

    //超过某个值就不下单了
    public BigDecimal getHigh() {
        BigDecimal price= klineService.getHigh(CandlestickInterval.HALF_HOURLY);
        Double d=price.doubleValue();
        d=d*0.99;
        return  new BigDecimal(d.intValue());
    }

    //价格过高，并且已经持有了
    public boolean isTooHigh(BigDecimal price) {
       //如果持仓是空的，就买点
        Stock stock = stockService.getStock();
        int hasBuy = stock.getHolds() - marketConfig.getBase();
        if(hasBuy==0){
            return false;
        }
        return price.compareTo(getHigh())>0;
    }
}
