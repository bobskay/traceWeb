package wang.wangby.trace.config;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.socket.MyWebSocketClient;
import wang.wangby.exchange.socket.listener.AccountListener;
import wang.wangby.exchange.socket.listener.MessageListener;

import java.net.URISyntaxException;
import java.util.List;

@Slf4j
public class SocketClient {
    public static int sleep=30*60*1000;
    public static int error=5*60*1000;
    public static boolean ERROR=false;

    private MyWebSocketClient client;
    private List<MessageListener> messageListeners;
    private List<AccountListener> accountListeners;
    private Exchange exchange;

    public SocketClient(Exchange exchange, List<MessageListener> messageListeners, List<AccountListener> accountListeners) throws URISyntaxException, InterruptedException {
       this.exchange=exchange;
       this.messageListeners=messageListeners;
       this.accountListeners=accountListeners;

        new Thread(() -> {
            while (true) {
                try {
                    if(ERROR){
                        log.error("出现异常重新创建socket");
                        createClient();
                        ERROR=false;
                    }
                    Thread.sleep(60*1000);
                } catch (Exception e) {
                    log.error("重启webSocket出错" + e.getMessage(), e);
                    try {
                        Thread.sleep(error);
                    } catch (InterruptedException ex) {
                        log.error(ex.getMessage(),ex);
                    }
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    createClient();
                    Thread.sleep(sleep);
                } catch (Exception e) {
                    log.error("重启webSocket出错" + e.getMessage(), e);
                    try {
                        Thread.sleep(error);
                    } catch (InterruptedException ex) {
                        log.error(ex.getMessage(),ex);
                    }
                }
            }
        }).start();
    }

    private void createClient() throws URISyntaxException, InterruptedException {
        if(client!=null){
            client.close();
        }
        log.info("准备创建socket");
        client = MyWebSocketClient.getInstance(exchange, messageListeners, accountListeners);
        client.start();

    }


}
