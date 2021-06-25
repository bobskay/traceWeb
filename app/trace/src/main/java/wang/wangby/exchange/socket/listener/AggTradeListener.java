package wang.wangby.exchange.socket.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import wang.wangby.exchange.enums.Stream;
import wang.wangby.utils.DateTime;

import java.math.BigDecimal;

/**
 * {
 * "e": "aggTrade",  // 事件类型
 * "E": 123456789,   // 事件时间
 * "s": "BNBUSDT",    // 交易对
 * "a":
 * "p": "0.001",     // 成交价格
 * "q": "100",       // 成交笔数
 * "f": 100,         // 被归集的首个交易ID
 * "l": 105,         // 被归集的末次交易ID
 * "T": 123456785,   // 成交时间
 * "m": true         // 买方是否是做市方。如true，则此次成交是一个主动卖出单，否则是一个主动买入单。
 * }
 */
public class AggTradeListener implements MessageListener {
    private Stream stream = Stream.aggTrade;
    //当前价格
    @Getter
    private volatile BigDecimal price;
    @Getter
    private volatile DateTime updated;

    //最近的最高价
    @Getter @Setter
    private volatile BigDecimal recentHigh=BigDecimal.ZERO;

    @Override
    public boolean listen(JSONObject js){
        this.price = js.getBigDecimal("p");
        this.updated = new DateTime(js.getLong("T"),DateTime.Format.YEAR_TO_MILLISECOND);

        if(price.compareTo(recentHigh)>0){
            recentHigh=price;
        }

        return true;
    }

    @Override
    public String eventName() {
        return stream.name();
    }


    @Override
    public Stream stream() {
        return stream;
    }
}
