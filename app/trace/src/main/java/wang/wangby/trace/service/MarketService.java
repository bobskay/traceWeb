package wang.wangby.trace.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.enums.OrderSide;
import wang.wangby.exchange.socket.listener.AggTradeListener;
import wang.wangby.trace.dto.StockOrderDto;
import wang.wangby.trace.config.Market;
import wang.wangby.trace.config.Rule;
import wang.wangby.trace.model.Stock;
import wang.wangby.trace.model.StockOrder;
import wang.wangby.trace.utils.OrderId;
import wang.wangby.trace.vo.CurrentOrder;
import wang.wangby.utils.DateTime;
import wang.wangby.utils.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MarketService {

    @Autowired
    StockService stockService;
    @Autowired
    Market market;
    @Autowired
    Exchange exchange;
    @Autowired
    Rule rule;

    @Autowired
    AggTradeListener aggTradeListener;

    public static int quantity(List<OpenOrder> orders) {
        int count=0;
        for (OpenOrder order:orders){
            count+=order.getOrigQty().intValue();
        }
        return count;
    }

    public CurrentOrder currentOrder() {
        CurrentOrder order = new CurrentOrder();
        BigDecimal price = aggTradeListener.getPrice();
        Stock stock = stockService.getStock();

        order.setPrice(price);
        order.setSell(min(OrderSide.SELL, stock.getOpenOrders()));
        order.setBuy(max(OrderSide.BUY, stock.getOpenOrders()));
        return order;
    }

    public static OpenOrder max(OrderSide side, List<OpenOrder> openOrders) {
        OpenOrder max = null;
        for (OpenOrder o : openOrders) {
            if (o.getSide() != side) {
                continue;
            }
            if (max == null || max.getPrice().compareTo(o.getPrice()) < 0) {
                max = o;
            }
        }
        return max;
    }

    public static OpenOrder min(OrderSide side, List<OpenOrder> openOrders) {
        OpenOrder min = null;
        for (OpenOrder o : openOrders) {
            if (o.getSide() != side) {
                continue;
            }
            if (min == null || min.getPrice().compareTo(o.getPrice()) > 0) {
                min = o;
            }
        }
        return min;
    }

    public BigDecimal getPrice() {
        return aggTradeListener.getPrice();
    }

    public List<StockOrder> getAll() {
        return market.getAll();
    }

    public void deleteOrder(long id) throws Exception {
        market.delete(id);
    }


    /**
     * @param currentPrice    当前价格
     * @param cancelId 取消后重新下单传远来的id
     */
    public String buy(BigDecimal currentPrice,String cancelId) {
        if(market.isTest()){
            return "测试环境不下单";
        }
        String id = null;
        if(StringUtil.isNotEmpty(cancelId)){
            id=OrderId.cancelId(cancelId);
        }else{
            id=OrderId.newId(OrderSide.BUY,currentPrice.intValue());
        }
        BigDecimal buyPrice=rule.buyPrice(currentPrice);
        BigDecimal quantity=rule.quantity(currentPrice);
        exchange.order(OrderSide.BUY, buyPrice, quantity, id);
        return id;
    }


    public String sell(BigDecimal currentPrice,BigDecimal quantity) {
        if (market.isTest()) {
            return "测试环境不下单";
        }

        String id = OrderId.newId(OrderSide.SELL, currentPrice.intValue());
        BigDecimal sellPrice=rule.sellPrice(currentPrice,quantity);
        exchange.order(OrderSide.SELL, sellPrice, quantity, id);
        return id;
    }

    //取消订单
    public void cancel(OpenOrder currentBuy) {
        exchange.cancel(currentBuy.getClientOrderId());
    }

    public List<StockOrder> query(StockOrderDto query) {
        List<StockOrder> list=new ArrayList<>();
        for(StockOrder o:getAll()){
            if(StringUtil.isNotEmpty(query.getClientOrderId())){
                if(!query.getClientOrderId().equalsIgnoreCase(o.getClientOrderId())){
                    continue;
                }
            }

            if(StringUtil.isNotEmpty(query.getType())){
                if(!query.getType().equalsIgnoreCase(o.getType())){
                    continue;
                }
            }

            if(StringUtil.isNotEmpty(query.getDate())){
                String date=new DateTime(o.getCreatedAt()).toString(DateTime.Format.YEAR_TO_DAY);
                if(!query.getDate().equalsIgnoreCase(date)){
                    continue;
                }
            }

            list.add(o);
        }
        return list;
    }
}
