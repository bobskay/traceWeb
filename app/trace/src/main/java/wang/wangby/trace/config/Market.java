package wang.wangby.trace.config;


import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.enums.OrderSide;
import wang.wangby.exchange.enums.OrderState;
import wang.wangby.exchange.socket.listener.AggTradeListener;
import wang.wangby.repostory.Repository;
import wang.wangby.serialize.json.JsonUtil;
import wang.wangby.trace.model.Stock;
import wang.wangby.trace.model.StockOrder;
import wang.wangby.trace.model.TraceOrder;
import wang.wangby.trace.service.MarketService;
import wang.wangby.trace.service.StockService;
import wang.wangby.trace.service.TraceOrderService;
import wang.wangby.trace.utils.OrderId;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class Market {
    @Getter
    private boolean test = MarketConfig.test;

    JsonUtil jsonUtil = JsonUtil.INSTANCE;

    @Autowired
    Repository repository;
    @Autowired
    StockService stockService;
    @Autowired
    MarketConfig marketConfig;
    @Autowired
    MarketService marketService;
    @Autowired
    Rule rule;
    @Autowired
    TraceOrderService traceOrderService;
    @Autowired
    AggTradeListener aggTradeListener;
    @Autowired
    Exchange exchange;


    //最后的买入时间，
    private long lastBuyTime = 0;


    public Market(Repository repository) {
        this.repository = repository;
    }

    public void orderUpdate(JSONObject js) throws Exception {
        //  log.info("收到订单变更消息：" + jsonUtil.toFormatString(js));
        JSONObject orderJs = js.getJSONObject("o");
        StockOrder order = addOrderStock(orderJs);

        if (OrderState.FILLED.toString().equalsIgnoreCase(order.getType())) {
            String clientId = order.getClientOrderId();
            if (order.getSide() == OrderSide.BUY) {
                String sellId = doSell(order);
                TraceOrder traceOrder = new TraceOrder();
                traceOrder.setBuyOrderId(order.getClientOrderId());
                traceOrder.setStart(OrderId.getPrice(order.getClientOrderId()));
                traceOrder.setCreatedAt(OrderId.getOrderTime(order.getClientOrderId()));
                traceOrder.setQuantity(order.getQuantity());
                traceOrder.setBuy(order.getPrice());
                traceOrder.setSellOrderId(sellId);
                traceOrder.setBuyFinish(new Date());
                repository.insert(traceOrder);
            }

            if (order.getSide() == OrderSide.SELL) {
                TraceOrder traceOrder = traceOrderService.getBySellId(order.getClientOrderId());
                if (traceOrder == null) {
                    log.error("找不到对应买单：" + order.getClientOrderId());
                    traceOrder = new TraceOrder();
                    traceOrder.setBuyOrderId(clientId);
                    traceOrder.setBuyOrderId(order.getClientOrderId());
                    traceOrder.setCreatedAt(order.getCreatedAt());
                    traceOrder.setQuantity(order.getQuantity());
                    traceOrder.setBuy(OrderId.getPrice(order.getClientOrderId()));
                    traceOrder.setStart(OrderId.getPrice(order.getClientOrderId()));
                    traceOrder.setSellOrderId(clientId);
                    repository.insert(traceOrder);
                }

                traceOrder.setSell(order.getPrice());
                traceOrder.setFinishAt(new Date());
                repository.update(traceOrder);
            }

            if (order.getSide() == OrderSide.CLOSE) {
                String sellId = order.getClientOrderId().replace(OrderSide.CLOSE.code, OrderSide.SELL.code);
                TraceOrder traceOrder = traceOrderService.getBySellId(sellId);
                if (traceOrder == null) {
                    log.error("找不到对应买单：" + sellId);
                    traceOrder = new TraceOrder();
                    traceOrder.setBuyOrderId(clientId);
                    traceOrder.setBuyOrderId(order.getClientOrderId());
                    traceOrder.setCreatedAt(order.getCreatedAt());
                    traceOrder.setQuantity(order.getQuantity());
                    traceOrder.setBuy(OrderId.getPrice(order.getClientOrderId()));
                    traceOrder.setStart(OrderId.getPrice(order.getClientOrderId()));
                    traceOrder.setSellOrderId(clientId);
                    repository.insert(traceOrder);
                }
                traceOrder.setSell(order.getPrice());
                traceOrder.setFinishAt(new Date());
                repository.update(traceOrder);
            }
        }
    }

    private String doSell(StockOrder order) {
        return marketService.sell(order.getPrice(), order.getQuantity());
    }


    private StockOrder addOrderStock(JSONObject orderJs) {
        String type = orderJs.getString("X");
        String orderId = orderJs.getString("c");
        BigDecimal price = orderJs.getBigDecimal("ap");
        BigDecimal quantity = orderJs.getBigDecimal("q");
        BigDecimal finish = orderJs.getBigDecimal("z");

        StockOrder stockOrder = new StockOrder();
        stockOrder.setType(type);
        stockOrder.setClientOrderId(orderId);
        stockOrder.setPrice(price);
        stockOrder.setQuantity(quantity);
        stockOrder.setOri(orderJs.toString());
        stockOrder.setCreatedAt(OrderId.getOrderTime(orderId));
        stockOrder.setFinish(finish);
        repository.insert(stockOrder);
        return stockOrder;
    }

    public List<StockOrder> getAll() {
        return repository.select(new StockOrder(), 0, 10000);
    }

    public void delete(long id) throws Exception {
        repository.delete(StockOrder.class, id);
    }

    Integer count = 0;

    @Scheduled(cron = "0/3 * * * * ? ")
    public void doTrace() throws InterruptedException {
        log.info("执行次数：" + count++);
        Stock stock = stockService.updateSock();
        BigDecimal price = marketService.getPrice();
        if (price == null) {
            log.info("等待系统启动");
            return;
        }

        OpenOrder buy = null;
        for (OpenOrder openOrder : stock.getOpenOrders()) {
            if (openOrder.getSide() == OrderSide.BUY) {
                buy = openOrder;
            }
        }

        if (buy == null) {
            String id = OrderId.newId(OrderSide.BUY, price);
            BigDecimal buyP = rule.buyPrice(price);
            exchange.order(OrderSide.BUY, buyP, rule.quantity(price), id);
            log.info("下完单休息10秒");
            Thread.sleep(10000);
            return;
        }
        BigDecimal buyCancel = new BigDecimal(marketConfig.getBuyCancel());
        if (price.subtract(buy.getPrice()).compareTo(buyCancel) > 0) {
            exchange.cancel(buy.getClientOrderId());
            log.info("价格差距过大，重新买入"+buy.getClientOrderId());
            Thread.sleep(2000);
            return;
        }

        log.info("等待买入：" + buy.getPrice()+"->"+price);
    }
}
