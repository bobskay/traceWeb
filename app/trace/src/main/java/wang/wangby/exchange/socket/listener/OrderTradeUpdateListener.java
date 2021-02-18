package wang.wangby.exchange.socket.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import wang.wangby.exchange.enums.OrderState;
import wang.wangby.trace.config.Market;

import java.math.BigDecimal;

@Slf4j
public class OrderTradeUpdateListener implements AccountListener {
    private Market market;

    public OrderTradeUpdateListener(Market market) {
        this.market = market;
    }

    @Override
    public boolean listen(JSONObject js) {
        try{
            market.orderUpdate(js);
        }catch (Exception ex){
            log.error("更新订单出错："+ex.getMessage(),ex);
        }

        return true;
    }

    public String eventName() {
        return "ORDER_TRADE_UPDATE";
    }

}
