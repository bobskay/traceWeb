package wang.wangby.trace.job;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.dto.Account;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.enums.OrderSide;
import wang.wangby.trace.config.Market;
import wang.wangby.trace.config.MarketConfig;
import wang.wangby.trace.config.Rule;
import wang.wangby.trace.model.Profit;
import wang.wangby.trace.model.Stock;
import wang.wangby.trace.service.MarketService;
import wang.wangby.trace.service.StockService;
import wang.wangby.trace.utils.OrderId;
import wang.wangby.utils.IdWorker;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ForceCloseJob {

    @Autowired
    Rule rule;
    @Autowired
    Exchange exchange;
    @Autowired
    MarketService marketService;
    @Autowired
    StockService stockService;


    @Scheduled(cron = "0 0/5 * * * ?")
    public void forceClose() throws Exception {
        Stock stock = check();
        if(stock==null){
            return;
        }
        log.info("持仓：{}，挂单{}",stock.getHolds(),stock.sellQuantity());

        OpenOrder order= rule.forceCloseOrder();
        if(order==null){
            return;
        }
        log.info("资金不足，强平订单："+order.getClientOrderId());
        if(MarketConfig.test){
            log.info("测试环境假装强平："+order.getClientOrderId().replace(OrderSide.SELL.code,OrderSide.CLOSE.code));
            return;
        }

        exchange.cancel(order.getClientOrderId());
        String id= order.getClientOrderId().replace(OrderSide.SELL.code,OrderSide.CLOSE.code);
        exchange.order(OrderSide.SELL,BigDecimal.ZERO,order.getOrigQty(),id);
        Thread.sleep(1000*10);
        //等10s，如果挂着的卖单和持仓不符就卖
        check();
    }

    private Stock check() throws InterruptedException {
        Stock stock=stockService.getStock();
        if(stock==null){
            return null;
        }

        //持仓已经超过最大可以买的了，将最早的强平
        List<OpenOrder> opens=stock.sells();
        BigDecimal sells=new BigDecimal(0);
        OpenOrder maxPrice=opens.get(0);
        for(int i=0;i<opens.size();i++){
            sells=sells.add(opens.get(i).getOrigQty());
            if(opens.get(i).getPrice().compareTo(maxPrice.getPrice())>0){
                maxPrice=opens.get(i);
            }
        }

        //超出部分
        BigDecimal tooMany=stock.getHolds().subtract(sells);
        if(tooMany.compareTo(BigDecimal.ZERO)>0){
            log.error("当前持仓：{}，卖单：{}，需要卖出：{}",stock.getHolds(),sells,
                    tooMany+"*"+maxPrice.getPrice().add(BigDecimal.ONE));
            if(MarketConfig.test){
                return stock;
            }
            String id= OrderId.newId(OrderSide.SELL,maxPrice.getPrice());
            exchange.order(OrderSide.SELL,maxPrice.getPrice().add(BigDecimal.ONE),tooMany,id);
            Thread.sleep(1000*10);
        }
        return stockService.getStock();
    }


}
