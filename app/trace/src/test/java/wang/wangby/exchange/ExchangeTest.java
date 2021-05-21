package wang.wangby.exchange;

import lombok.extern.slf4j.Slf4j;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.response.Order;
import wang.wangby.serialize.json.FastJsonImpl;
import wang.wangby.serialize.json.JsonUtil;
import wang.wangby.trace.config.MarketConfig;
import wang.wangby.trace.config.OrderConfig;
import wang.wangby.trace.utils.BillNo;

import java.math.BigDecimal;

@Slf4j
public class ExchangeTest {

    private static JsonUtil jsonUtil=new FastJsonImpl();
    static OrderConfig orderConfig=new OrderConfig();
    static Exchange exchange=new Exchange();

    public static void main(String[] args) throws InterruptedException {
        String billNo="B_20210521003820";
        String sellNo="B_20210521003820_2547_C0";

        Order order= exchange.getOrder("S_20210522033455_2342_C3");
        System.out.println(jsonUtil.toFormatString(order));
      //  exchange.reSell();
//        for(OpenOrder op: exchange.openOrders()){
//            System.out.println(jsonUtil.toFormatString(op));
//        }
//        exchange.preBuy("S_20210521003820_2771_C0",new BigDecimal(2803),new BigDecimal(0.01));


    }


    void preSell(Order buyOrder,String billNo){
        BigDecimal sellPrice = buyOrder.getAvgPrice().subtract(orderConfig.getUpgradePrice());
        String sellNo = BillNo.close(billNo, sellPrice);
        exchange.preSell(sellNo, sellPrice, orderConfig.getOpenQuantity());
    }


}