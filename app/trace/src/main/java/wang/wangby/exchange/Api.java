package wang.wangby.exchange;


import wang.wangby.exchange.dto.*;
import wang.wangby.exchange.response.ApiResponse;
import wang.wangby.exchange.response.Order;
import wang.wangby.exchange.response.Ticker;

public enum Api {
    BATCH_ORDERS("批量下单","/fapi/v1/batchOrders", Order.class),
    TICKER("行情信息","/fapi/v1/ticker/bookTicker", Ticker.class),
    PRICE ("最新价","/fapi/v1/ticker/price", Price.class),
    ORDER("下单","/fapi/v1/order", Order.class),
    BALANCE("账户余额","/fapi/v2/balance", Balance.class),
    ACCOUNT("账户信息","/fapi/v2/account ", Account.class),
    CANCEL_ALL("取消所有订单","/fapi/v1/allOpenOrders", CancelAll.class),
    CANCEL("取消订单","/fapi/v1/order", Cancel.class),
    OPEN_ORDER("挂单信息","/fapi/v1/openOrder", OpenOrder.class),
    OPEN_ORDERS("所有挂单","/fapi/v1/openOrders", OpenOrders.class),
    KLINE("k线","/fapi/v1/klines", Kline.class),

    LISTEN_KEY("获取webSocket的token",Constants.API_BASE_URL+"/fapi/v1/listenKey", UserDataStream.class),

    ;
    public final Class<? extends ApiResponse> response;
    public final String url;
    public final String name;
    Api(String name,String url,Class response){
        this.name=name;
        this.url=url;
        this.response=response;
    }
}
