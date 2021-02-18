package wang.wangby.exchange.socket.listener;

import com.alibaba.fastjson.JSONObject;
import wang.wangby.exchange.enums.Stream;

public class KlineListener implements MessageListener {
    private Stream stream = Stream.kline_1m;


    @Override
    public boolean listen(JSONObject js){
        return true;
    }

    @Override
    public String eventName() {
      return "kline";
    }


    @Override
    public Stream stream() {
        return stream;
    }

}
