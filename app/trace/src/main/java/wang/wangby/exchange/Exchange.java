package wang.wangby.exchange;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wang.wangby.exchange.dto.*;
import wang.wangby.exchange.enums.*;
import wang.wangby.exchange.response.Order;
import wang.wangby.exchange.response.Ticker;
import wang.wangby.exchange.utils.HttpClient;
import wang.wangby.exchange.utils.OrderBuilder;
import wang.wangby.exchange.utils.UrlParamsBuilder;
import wang.wangby.trace.config.MarketConfig;
import wang.wangby.trace.utils.BillNo;
import wang.wangby.utils.DateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Exchange {
    private HttpClient client;
    @Getter
    private String symbol;
    private Integer scale;

    public Exchange() {
        client = new HttpClient();
        symbol = "ETHUSDT";
        scale = 2;
    }

    public Ticker ticker() {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("symbol", symbol);
        return client.get(Api.TICKER, builder);
    }

    //账户信息
    public Account account() {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("symbol", symbol);
        Account account = client.get(Api.ACCOUNT, builder);
        return account;
    }

    public boolean closeAll() {
        for (Account.PositionsDTO posi : this.account().getPositions()) {
            if (symbol.equalsIgnoreCase(posi.getSymbol())) {
                if (posi.getPositionAmt().compareTo(BigDecimal.ZERO) > 0) {
                    String orderId = OrderFlag.F + DateTime.current().toString(DateTime.Format.YEAR_TO_MILLISECOND_STRING);
                    this.order(OrderSide.SELL, BigDecimal.ZERO, posi.getPositionAmt(), orderId);
                    log.info("市价平仓：" + posi.getPositionAmt());
                    return true;
                }
                if (posi.getPositionAmt().compareTo(BigDecimal.ZERO) < 0) {
                    BigDecimal amount = BigDecimal.ZERO.subtract(posi.getPositionAmt());
                    String orderId = OrderFlag.F + DateTime.current().toString(DateTime.Format.YEAR_TO_MILLISECOND_STRING);
                    this.order(OrderSide.BUY, BigDecimal.ZERO, amount, orderId);
                    log.info("市价平仓：" + amount);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public Balance balance() {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("symbol", symbol);
        return client.get(Api.BALANCE, builder);
    }

    public Price price() {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("symbol", symbol);
        return client.get(Api.PRICE, builder);
    }

    public String socketListenKey() {
        UserDataStream dataStream = client.invoke(Api.LISTEN_KEY, HttpUtil::createPost);
        return dataStream.getListenKey();
    }

    public void cancelAll() throws Exception {
        for (int i = 0; i < 5; i++) {
            UrlParamsBuilder builder = UrlParamsBuilder.build();
            builder.putToUrl("symbol", symbol);
            CancelAll cancelAll = client.delete(Api.CANCEL_ALL, builder);
            if (cancelAll.getCode() == 200) {
                return;
            }
            log.error("取消失败，准备重试:" + cancelAll);
            Thread.sleep(1000);
        }
        throw new RuntimeException("调用取消订单接口出错");
    }


    /**
     * @param price 下单价，如果为0说明是市价单
     */
    public Order order(OrderSide orderSide, BigDecimal price, BigDecimal quantity, String id) {
        BigDecimal newPrice = price.setScale(scale, RoundingMode.HALF_DOWN);
        if (newPrice.compareTo(price) != 0) {
            log.info("小数点过多，修改价格：" + price + "-->" + newPrice);
        }
        BigDecimal newQuantity = quantity.setScale(scale, RoundingMode.HALF_DOWN);
        if (newQuantity.compareTo(quantity) != 0) {
            log.info("小数点过多，修改数量：" + quantity + "-->" + newQuantity);
        }
        OrderDto orderDto = OrderBuilder.order(orderSide, newPrice, newQuantity);
        orderDto.setSymbol(symbol);
        orderDto.setNewClientOrderId(id);


        if (price.compareTo(BigDecimal.ZERO) == 0) {
            orderDto.setType(OrderType.MARKET);
            orderDto.setPrice(null);
            orderDto.setTimeInForce(null);
        }
        Order order = client.post(Api.ORDER, OrderBuilder.builder(orderDto));
        if (order.getStatus() != OrderState.NEW) {
            log.error("下单失败：" + order.getStatus() + ":" + order.getResponse());
        }
        return order;
    }

    public boolean cancel(String clientOrderId) {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("origClientOrderId", clientOrderId);
        builder.putToUrl("symbol", symbol);
        Cancel cancel = client.delete(Api.CANCEL, builder);
        if (!OrderState.CANCELED.toString().equalsIgnoreCase(cancel.getStatus())) {
            if (!cancel.getResponse().equalsIgnoreCase("{\"code\":-2011,\"msg\":\"Unknown order sent.\"}")) {
                log.error("取消失败：\n请求内容：{}\n返回结果：{}", cancel.getRequest(), cancel.getResponse());
                return false;
            }

        }
        return true;

    }

    public Order getOrder(String origClientOrderId) {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("origClientOrderId", origClientOrderId);
        builder.putToUrl("symbol", symbol);
        Order order = client.get(Api.GET_ORDER, builder);
        return order;
    }

    public OpenOrder openOrder(String clientOrderId) {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("origClientOrderId", clientOrderId);
        builder.putToUrl("symbol", symbol);
        return client.get(Api.OPEN_ORDER, builder);
    }

    public List<OpenOrder> openOrders() {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("symbol", symbol);
        OpenOrders orders = client.get(Api.OPEN_ORDERS, builder);
        return orders.getOpenOrders();
    }

    /**
     * [
     * [
     * 1607444700000,      // 开盘时间
     * "18879.99",         // 开盘价
     * "18900.00",         // 最高价
     * "18878.98",         // 最低价
     * "18896.13",         // 收盘价(当前K线未结束的即为最新价)
     * "492.363",          // 成交量
     * 1607444759999,      // 收盘时间
     * "9302145.66080",    // 成交额
     * 1874,               // 成交笔数
     * "385.983",          // 主动买入成交量
     * "7292402.33267",    // 主动买入成交额
     * "0"                 // 请忽略该参数
     * ]
     * ]
     */
    public List<Kline> kline(CandlestickInterval interval) {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("symbol", symbol);
        builder.putToUrl("interval", interval);
        List<Kline> lines = new ArrayList<>();
        client.invoke(Api.KLINE, builder, HttpUtil::createGet, resp -> {
            JSONArray jsonArray = JSONArray.parseArray(resp);
            for (Object o : jsonArray) {
                JSONArray arr = (JSONArray) o;
                Kline line = new Kline();
                line.setTime(new DateTime(arr.getLong(0), DateTime.Format.YEAR_TO_SECOND));
                line.setOpen(arr.getBigDecimal(1));
                line.setHigh(arr.getBigDecimal(2));
                line.setLow(arr.getBigDecimal(3));
                line.setClose(arr.getBigDecimal(4));
                lines.add(line);
            }
            return new Kline();
        });
        return lines;
    }

    public void buy(String billNo, BigDecimal price, BigDecimal openQuantity) {
        this.order(OrderSide.BUY, price, openQuantity, billNo);
    }

    public void sell(String billNo, BigDecimal price, BigDecimal openQuantity) {
        this.order(OrderSide.SELL, price, openQuantity, billNo);
    }

    //空单止损
    public Order preBuy(String billNo, BigDecimal price, BigDecimal quantity) {
        BigDecimal newPrice = price.setScale(scale, RoundingMode.HALF_DOWN);
        if (newPrice.compareTo(price) != 0) {
            log.info("小数点过多，修改价格：" + price + "-->" + newPrice);
        }
        BigDecimal newQuantity = quantity.setScale(scale, RoundingMode.HALF_DOWN);
        if (newQuantity.compareTo(quantity) != 0) {
            log.info("小数点过多，修改数量：" + quantity + "-->" + newQuantity);
        }
        OrderDto orderDto = OrderBuilder.order(OrderSide.BUY, newPrice, newQuantity);
        orderDto.setSymbol(symbol);
        orderDto.setNewClientOrderId(billNo);
        orderDto.setType(OrderType.STOP_MARKET);
        orderDto.setPrice(null);
        orderDto.setTimeInForce(null);
        orderDto.setStopPrice(newPrice);
        orderDto.setWorkingType(WorkingType.CONTRACT_PRICE);

        Order order = client.post(Api.ORDER, OrderBuilder.builder(orderDto));
        if (order.getStatus() != OrderState.NEW) {
            log.error("下单失败：" + order.getStatus() + ":" + order.getResponse());
        }
        return order;
    }

    //多单止损
    public Order preSell(String billNo, BigDecimal price, BigDecimal quantity) {
        BigDecimal newPrice = price.setScale(scale, RoundingMode.HALF_DOWN);
        if (newPrice.compareTo(price) != 0) {
            log.info("小数点过多，修改价格：" + price + "-->" + newPrice);
        }
        BigDecimal newQuantity = quantity.setScale(scale, RoundingMode.HALF_DOWN);
        if (newQuantity.compareTo(quantity) != 0) {
            log.info("小数点过多，修改数量：" + quantity + "-->" + newQuantity);
        }
        OrderDto orderDto = OrderBuilder.order(OrderSide.SELL, newPrice, newQuantity);
        orderDto.setSymbol(symbol);
        orderDto.setNewClientOrderId(billNo);
        orderDto.setType(OrderType.STOP_MARKET);
        orderDto.setPrice(null);
        orderDto.setTimeInForce(null);
        orderDto.setStopPrice(newPrice);
        orderDto.setWorkingType(WorkingType.CONTRACT_PRICE);

        Order order = client.post(Api.ORDER, OrderBuilder.builder(orderDto));
        if (order.getStatus() != OrderState.NEW) {
            log.error("下单失败：" + order.getStatus() + ":" + order.getResponse());
        }
        return order;
    }

    public String reSell(String billNo, BigDecimal newPrice,BigDecimal quantity) throws InterruptedException {
        for(int i=0;i<10;i++){
            if(this.cancel(billNo)){
                billNo= BillNo.increase(billNo);
                preSell(billNo,newPrice,quantity);
                break;
            }
            Thread.sleep(100);
        }

        return billNo;
    }

    public String reBuy(String billNo, BigDecimal newPrice, BigDecimal quantity) throws InterruptedException {
        for(int i=0;i<10;i++){
            if(this.cancel(billNo)){
                billNo= BillNo.increase(billNo);
                preBuy(billNo,newPrice,quantity);
                break;
            }
            Thread.sleep(100);
        }
        return billNo;
    }
}
