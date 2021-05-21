package wang.wangby.trace.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.enums.CandlestickInterval;
import wang.wangby.trace.model.Stock;
import wang.wangby.trace.service.KlineService;
import wang.wangby.trace.service.StockService;

import java.math.BigDecimal;
import java.util.List;

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
        if(stockService.getStock().sellQuantity().compareTo(marketConfig.getIgnoreMin())<0){
            return  marketConfig.minQuantity;
        }

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

    //还可以买入多少
    public BigDecimal totalRemain() {
        Stock stock = stockService.getStock();
        return marketConfig.getMaxHold().subtract(stock.getHolds()).subtract(stock.buyQuantity());
    }

    //如果可买数量小于0，就将最早的单子强平
    public OpenOrder forceCloseOrder(){
        //还有可以买的
        BigDecimal total=totalRemain();
        if(total.compareTo(BigDecimal.ZERO)>0){
            return null;
        }
        Stock stock = stockService.getStock();
        //还有挂着的买单
        if(stock.buyQuantity().compareTo(BigDecimal.ZERO)>0){
            return null;
        }

        //持仓已经超过最大可以买的了，将最早的强平
        List<OpenOrder> opens=stock.sells();
        if(opens.size()==0){
            log.error("可以买的数量<0,但挂着的买单为0");
            return null;
        }

        OpenOrder order=opens.get(0);
        for(int i=1;i<opens.size();i++){
            if(opens.get(i).getPrice().compareTo(order.getPrice())>0){
                order=opens.get(i);
            }
        }
        return order;
    }
}
