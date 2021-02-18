package wang.wangby.exchange.socket.listener;

import com.alibaba.fastjson.JSONObject;
import wang.wangby.exchange.enums.Stream;

public interface MessageListener extends AccountListener {
    boolean listen(JSONObject jsonObject);

    Stream stream();


}
