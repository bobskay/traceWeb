package wang.wangby.trace.model;

import lombok.Data;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.dto.OpenOrders;
import wang.wangby.exchange.enums.OrderSide;
import wang.wangby.trace.service.MarketService;

import java.math.BigDecimal;
import java.util.*;

@Data
public class Stock {
    private int holds;
    private List<OpenOrder> openOrders;

    public List<OpenOrder> buys() {
        List<OpenOrder> ops=new ArrayList<>();
        for(OpenOrder o:openOrders){
            if(o.getSide()== OrderSide.BUY){
                ops.add(o);
            }
        }
        return ops;
    }

    public int buyQuantity(){
        return MarketService.quantity(buys());
    }


    public int sellQuantity(){
        return MarketService.quantity(sells());
    }

    public List<OpenOrder> sells() {
        List<OpenOrder> ops=new ArrayList<>();
        for(OpenOrder o:openOrders){
            if(o.getSide()== OrderSide.SELL){
                ops.add(o);
            }
        }
        return ops;
    }

    public OpenOrder sellPrice() {
       return MarketService.min(OrderSide.SELL,openOrders);
    }

    public OpenOrder buyPrice() {
        return MarketService.max(OrderSide.BUY,openOrders);
    }
}
