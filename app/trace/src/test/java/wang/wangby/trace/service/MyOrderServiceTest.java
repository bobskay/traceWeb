package wang.wangby.trace.service;

import ch.qos.logback.core.util.CloseUtil;
import wang.wangby.exchange.Exchange;
import wang.wangby.serialize.json.FastJsonImpl;
import wang.wangby.serialize.json.JsonUtil;
import wang.wangby.trace.config.OrderConfig;
import wang.wangby.trace.model.MyOrder;

import java.math.BigDecimal;

public class MyOrderServiceTest {

    private static JsonUtil jsonUtil=new FastJsonImpl();

    public static void main(String[] args) throws InterruptedException {
        MyOrderService myOrderService=new MyOrderService();
        myOrderService.exchange=new Exchange();
        myOrderService.jsonUtil=new FastJsonImpl();
        myOrderService.orderConfig=new OrderConfig();
        BigDecimal price=myOrderService.exchange.price().getPrice();

        myOrderService.newBuy();
        for(MyOrder order:myOrderService.openOrders()){
            System.out.println(jsonUtil.toFormatString(order));
        }
    }

}