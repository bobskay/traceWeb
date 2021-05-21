package wang.wangby.trace.config;


import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.enums.OrderState;
import wang.wangby.exchange.response.Order;
import wang.wangby.repostory.Repository;
import wang.wangby.serialize.json.JsonUtil;
import wang.wangby.trace.model.MyOrder;
import wang.wangby.trace.model.Stock;
import wang.wangby.trace.service.MarketService;
import wang.wangby.trace.service.MyOrderService;
import wang.wangby.trace.service.StockService;
import wang.wangby.trace.service.TraceOrderService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
public class Market {
    JsonUtil jsonUtil = JsonUtil.INSTANCE;

    @Autowired
    Repository repository;
    @Autowired
    StockService stockService;
    @Autowired
    MarketConfig marketConfig;
    @Autowired
    Rule rule;
    @Autowired
    TraceOrderService traceOrderService;

    @Autowired
    RunningInfo runningInfo;

    @Autowired
    MyOrderService myOrderService;

    @Autowired
    OrderConfig orderConfig;

    @Autowired
    Exchange exchange;

    @Autowired
    MarketService marketService;


    public Market(Repository repository) {
        this.repository = repository;
    }

    public void orderUpdate(JSONObject js) throws Exception {
        JSONObject orderJs = js.getJSONObject("o");
        String orderId = orderJs.getString("c");
        Order order = exchange.getOrder(orderId);
        if (OrderState.FILLED.toString().equalsIgnoreCase(orderJs.getString("X"))) {
            myOrderService.finish(order);
        }
    }

    @Scheduled(cron = "0/5 * * * * ? ")
    public void doTrace() throws InterruptedException {
        BigDecimal price = marketService.getPrice();
        Stock stock = stockService.updateSock();
        if (price == null || stock == null) {
            log.info("等待系统启动");
            return;
        }

        if (stock == null) {
            log.info("账户信息未初始化");
            return;
        }

        if (runningInfo.getBasePrice() == null) {
            runningInfo.setBasePrice(price);
        }
        List<MyOrder> openOrders = myOrderService.openOrders();
        boolean hasActive=false;
        boolean upgrade=false;
        for (MyOrder order : openOrders) {
            //创建
            if (order.isActive()) {
                boolean up=myOrderService.checkUpgrade(order, price);
                if(!upgrade){
                    upgrade=up;
                }
            } else {
                boolean active = myOrderService.checkCreate(order, price);
                if (active) {
                    myOrderService.newOrder(order);
                }
                if(order.isBuy()){
                    runningInfo.setBasePrice(order.getBuyPrice());
                }else{
                    runningInfo.setBasePrice(order.getSellPrice());
                }
                hasActive=true;
            }
        }

        if(hasActive){
            return;
        }

        if(openOrders.size()>0){
            if(upgrade){
                myOrderService.newOrder(openOrders.get(0));
            }
            return;
        }

        if (price.subtract(runningInfo.getBasePrice()).compareTo(orderConfig.getUpgradePrice()) > 0) {
            myOrderService.newBuy();
        } else if (runningInfo.getBasePrice().subtract(price).compareTo(orderConfig.getUpgradePrice()) > 0) {
            myOrderService.newSell();
        } else {
            BigDecimal diff = price.subtract(runningInfo.getBasePrice());
            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                log.info(price + "等待买入：" + orderConfig.getUpgradePrice().subtract(diff));
            }
            if (diff.compareTo(BigDecimal.ZERO) < 0) {
                log.info(price + "等待卖出：" + orderConfig.getUpgradePrice().add(diff));
            }
        }
    }


}
