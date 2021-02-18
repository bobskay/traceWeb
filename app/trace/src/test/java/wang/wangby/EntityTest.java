package wang.wangby;

import wang.wangby.serialize.json.JsonUtil;
import wang.wangby.trace.model.Stock;
import wang.wangby.trace.model.StockOrder;

public class EntityTest {
    public static void main(String[] args) {
        String x="{\"createdAt\":1613447116119,\"ext\":{},\"id\":\"281752858238387200\",\"orderId\":\"B20210216114423-1816\",\"ori\":\"{\\\"ps\\\":\\\"BOTH\\\",\\\"L\\\":\\\"1814\\\",\\\"N\\\":\\\"BNB\\\",\\\"R\\\":false,\\\"S\\\":\\\"BUY\\\",\\\"T\\\":\\\"1613447118039\\\",\\\"si\\\":0,\\\"X\\\":\\\"PARTIALLY_FILLED\\\",\\\"sp\\\":\\\"0\\\",\\\"wt\\\":\\\"CONTRACT_PRICE\\\",\\\"pP\\\":false,\\\"ss\\\":0,\\\"a\\\":\\\"9155\\\",\\\"b\\\":\\\"0\\\",\\\"c\\\":\\\"B20210216114423-1816\\\",\\\"ot\\\":\\\"LIMIT\\\",\\\"f\\\":\\\"GTC\\\",\\\"i\\\":\\\"8389765492748701412\\\",\\\"l\\\":\\\"0.712\\\",\\\"m\\\":true,\\\"cp\\\":false,\\\"n\\\":\\\"0.00177677\\\",\\\"o\\\":\\\"LIMIT\\\",\\\"ap\\\":\\\"1814\\\",\\\"p\\\":\\\"1814\\\",\\\"q\\\":\\\"1\\\",\\\"s\\\":\\\"ETHUSDT\\\",\\\"t\\\":340523502,\\\"x\\\":\\\"TRADE\\\",\\\"z\\\":\\\"0.811\\\",\\\"rp\\\":\\\"0\\\"}\",\"price\":1814,\"quantity\":1,\"status\":\"PARTIALLY_FILLED\"}";
        StockOrder o= JsonUtil.INSTANCE.toBean(x,StockOrder.class);
        System.out.println(o.getOri());
    }
}
