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
import wang.wangby.trace.service.MarketService;
import wang.wangby.trace.utils.OrderId;
import wang.wangby.utils.IdWorker;

import java.math.BigDecimal;
import java.util.Date;

@Component
@Slf4j
public class ForceCloseJob {

    @Autowired
    Rule rule;
    @Autowired
    Exchange exchange;
    @Autowired
    MarketService marketService;


    @Scheduled(cron = "0/5 * * * * ?")
    public void forceClose() throws Exception {
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
        BigDecimal currentPrice=marketService.getPrice();
        BigDecimal sellPrice=currentPrice.subtract(new BigDecimal(100));
        String id= order.getClientOrderId().replace(OrderSide.SELL.code,OrderSide.CLOSE.code);
        exchange.order(OrderSide.SELL,sellPrice,order.getOrigQty(),id);
    }


}
