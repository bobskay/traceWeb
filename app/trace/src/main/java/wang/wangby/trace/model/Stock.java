package wang.wangby.trace.model;

import lombok.Data;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.enums.OrderSide;

import java.math.BigDecimal;
import java.util.*;

@Data
public class Stock {
    private BigDecimal holds;
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

    public BigDecimal buyQuantity(){
        return quantity(buys());
    }


    public BigDecimal sellQuantity(){
        return quantity(sells());
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
       return min(OrderSide.SELL,openOrders);
    }

    public OpenOrder buyPrice() {
        return max(OrderSide.BUY,openOrders);
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

    public static BigDecimal quantity(List<OpenOrder> orders) {
        BigDecimal count=BigDecimal.ZERO;
        for (OpenOrder order:orders){
            count=count.add(order.getOrigQty());
        }
        return count;
    }

}
