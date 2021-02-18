package wang.wangby.exchange;

import org.junit.Test;
import wang.wangby.serialize.json.JsonUtil;

import static org.junit.Assert.*;

public class ExchangeTest {

    public static void main(String[] args) {
        Exchange exchange=new Exchange();
      //  exchange.cancel("S20210217204223-1847");
      System.out.println(JsonUtil.INSTANCE.toFormatString(exchange.account()));
    }
}