package wang.wangby.tools.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class MyWebSocketHandler extends TextWebSocketHandler {
    private final static Map<String, SocketClient> sessionMap=new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        SocketClient clinet= sessionMap.get(session.getId());
        if(!clinet.getReceive().offer(message)){
            log.warn("消息处理过慢:"+session.getId());
        }
    }

    public static void iterator(Consumer<SocketClient> socketClientConsumer){
        for(SocketClient client:sessionMap.values()){
            socketClientConsumer.accept(client);
        }
    }

    public SocketClient getClient(WebSocketSession session){
        SocketClient client=sessionMap.get(session.getId());
        if(client==null){
            client=new SocketClient();
            client.setWebSocketSession(session);
            sessionMap.put(session.getId(),client);
        }
        return client;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //保存所有会话
        getClient(session);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        SocketClient client =sessionMap.get(session.getId());
        if(client!=null){
            client.stop();
        }
        sessionMap.remove(session.getId());
    }
}
