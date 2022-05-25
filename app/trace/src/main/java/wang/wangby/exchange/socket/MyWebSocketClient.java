package wang.wangby.exchange.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.socket.listener.AccountListener;
import wang.wangby.exchange.socket.listener.MessageListener;
import wang.wangby.serialize.json.JsonUtil;
import wang.wangby.trace.config.SocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class MyWebSocketClient extends WebSocketClient {
    private JsonUtil jsonUtil = JsonUtil.INSTANCE;
    @Getter
    private List<MessageListener> messageListeners;
    @Getter
    private List<AccountListener> accountListeners;
    private Exchange exchange;

    public static MyWebSocketClient getInstance(Exchange exchange, List<MessageListener> messageListeners, List<AccountListener> accountListeners) throws URISyntaxException {
        String key = exchange.socketListenKey();
        String url = "wss://fstream.binance.com/ws/" + key;
        MyWebSocketClient client = new MyWebSocketClient(url);
        client.messageListeners = messageListeners;
        client.accountListeners = accountListeners;
        client.exchange = exchange;
        return client;
    }


    public MyWebSocketClient(String url) throws URISyntaxException {
        super(new URI(url));
    }

    @Override
    public void onOpen(ServerHandshake shake) {
        log.info("握手...");
        for (Iterator<String> it = shake.iterateHttpFields(); it.hasNext(); ) {
            String key = it.next();
            log.info(key + ":" + shake.getFieldValue(key));
        }
    }

    @Override
    public void onMessage(String msg) {
        JSONObject js = JSON.parseObject(msg);
        String event = js.getString("e");
        if (event == null) {
            log.info("收到消息：" + msg);
            return;
        }
        AccountListener listener = getListener(event);
        if (listener == null) {
            log.error("找不掉对应的消费者：" + msg);
            return;
        }
        if (!listener.listen(js)) {
            log.error("消费失败：" + msg);
        }
    }

    private AccountListener getListener(String e) {
        List<String> name = new ArrayList<>();
        for (AccountListener ac : accountListeners) {
            if (ac.eventName().equalsIgnoreCase(e)) {
                return ac;
            }
            name.add(ac.eventName());
        }
        for (AccountListener ac : messageListeners) {
            if (ac.eventName().equalsIgnoreCase(e)) {
                return ac;
            }
            name.add(ac.eventName());
        }
        log.error("没有匹配的listener,当前{}，需要{}", name, e);
        return null;
    }

    @Override
    public void onClose(int paramInt, String paramString, boolean paramBoolean) {
        log.info("关闭...");
    }

    @Override
    public void onError(Exception e) {
        log.error("出现异常" + e.getMessage(), e);
        SocketClient.ERROR=true;
    }



    public void start() throws InterruptedException {
        super.connect();
        Thread.sleep(800);
        while (!getReadyState().equals(ReadyState.OPEN)) {
            log.info("socket状态:" + getReadyState());
            Thread.sleep(100);
        }
        Subscribe subscribe = new Subscribe();
        subscribe.setId(1);
        List<String> subs = new ArrayList<>();
        for (MessageListener listener : messageListeners) {
            subs.add(exchange.getSymbol().toLowerCase() + "@" + listener.stream());
        }
        subscribe.setParams(subs);
        String subInfo = jsonUtil.toString(subscribe);
        log.info("发送ws消息：" + subInfo);
        super.send(subInfo);
    }
}