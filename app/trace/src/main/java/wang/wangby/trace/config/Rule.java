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
        //高价区只买1
        if (isTooHigh(currentPrice)) {
            return BigDecimal.ONE;
        }

        Stock stock = stockService.getStock();
        //如果10个价位内没有买单就买3
        //其他买1个
        for (OpenOrder order : stock.sells()) {
            int price =order.getPrice().intValue();
            int diff = Math.abs(price - currentPrice.intValue()-marketConfig.getSellPlus());
            if (diff < 5) {
                return BigDecimal.ONE;
            }
            if (diff < 10) {
                return new BigDecimal(2);
            }
        }
        return new BigDecimal(3);
    }

    public BigDecimal sellPrice(BigDecimal currentPrice, BigDecimal quantity) {
        if (quantity.intValue() == 1) {
            int sell = currentPrice.intValue() + marketConfig.getSellPlus();
            return new BigDecimal(sell);
        }
        if (quantity.intValue() == 2) {
            int sell = currentPrice.intValue() +4;
            return new BigDecimal(sell);
        }
        int sell = currentPrice.intValue() + 3;
        return new BigDecimal(sell);
    }

    //超过某个值就不下单了
    public BigDecimal stopPrice() {
        BigDecimal price = klineService.getHigh(CandlestickInterval.HALF_HOURLY);
        Double d = price.doubleValue();
        d = d * 0.99;
        return new BigDecimal(d.intValue());
    }

    //价格过高，并且已经持有了
    public boolean isTooHigh(BigDecimal price) {
        //如果持仓小于2就买点
        Stock stock = stockService.getStock();
        int hasBuy = stock.getHolds() - marketConfig.getBase();
        if (hasBuy == 0) {
            return false;
        }
        return price.compareTo(stopPrice()) > 0;
    }

    //当前价位是否已经买太多
    //5个价位内只能买3个，10个价位内只能买7个
    public boolean isTooMany(BigDecimal currentPrice) {
        return currentRemain(currentPrice) <0;
    }

    //10个价位内只能买8个
    public int currentRemain(BigDecimal currentPrice) {
        Stock stock = stockService.getStock();
        int count=0;
        for (OpenOrder order : stock.sells()) {
            int price =order.getPrice().intValue();
            int diff = Math.abs(price - currentPrice.intValue());
            if (diff < 10) {
                count += order.getOrigQty().intValue();
            }
        }
        return 8-count;
    }

    public int totalRemain() {
        Stock stock = stockService.getStock();
        return marketConfig.getMaxHold() - stock.getHolds() - stock.buyQuantity();
    }
}
