package wang.wangby.trace.config;


import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.enums.OrderSide;
import wang.wangby.exchange.enums.OrderState;
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
    @Value("${test}")
    @Getter
    private boolean test;

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
        }
    }

    private String doSell(StockOrder order) {
        return marketService.sell(order.getPrice(), order.getQuantity());
    }


    private StockOrder addOrderStock(JSONObject orderJs) {
        String type = orderJs.getString("X");
        String orderId = orderJs.getString("c");
        BigDecimal price = orderJs.getBigDecimal("p");
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


    @Scheduled(cron = "0/3 * * * * ? ")
    public void doTrace() {
        BigDecimal price = marketService.getPrice();
        Stock stock = stockService.getStock();
        if(price==null||stock==null){
            log.info("等待系统启动");
            return;
        }

        if (System.currentTimeMillis() - lastBuyTime < marketConfig.getBuyInterval()) {
            log.info("短时间内成交过，跳过买入判断："
                    + (System.currentTimeMillis() - lastBuyTime) / 1000);
            return;
        }

        if (stock == null) {
            log.info("账户信息未初始化");
            return;
        }

        if (rule.totalRemain().compareTo(BigDecimal.ZERO)<=0) {
            log.info("已到达最大持仓，请求注意风险：" + stock.getHolds());
            return;
        }


        OpenOrder currentBuy = stock.buyPrice();

        //无买单
        if (currentBuy == null) {
            marketService.buy(price,null);
            lastBuyTime = System.currentTimeMillis();
            return;
        }

        //价格高与下单价好多
        if (rule.isCancel(price, currentBuy)) {
            marketService.cancel(currentBuy);
            marketService.buy(price,currentBuy.getClientOrderId());
            lastBuyTime = System.currentTimeMillis();
            return;
        }
        log.info("当前价格:" + price +
                ",等待买入:" + currentBuy.getPrice() +
                ",取消价格：" + rule.cancelPrice(price,currentBuy));
    }
}
