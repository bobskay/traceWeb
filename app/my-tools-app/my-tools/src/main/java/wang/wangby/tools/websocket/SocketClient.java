package wang.wangby.tools.websocket;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

@Data
@Slf4j
public class SocketClient {
    private ArrayBlockingQueue<TextMessage> receive=new ArrayBlockingQueue(100);
    private WebSocketSession webSocketSession;
    private List<SocketTask> tasks=new ArrayList<>();

    public void send(String msg) {
        TextMessage text=new TextMessage(msg);
        if(webSocketSession.isOpen()){
            try {
                webSocketSession.sendMessage(text);
            } catch (IOException e) {
                log.error("发送消息出错:"+text);
            }
        }else{
            stop();
        }
    }

    public void addTask(SocketTask task){
        tasks.add(task);
    }

    public void stop() {
        for(SocketTask task:tasks){
            task.stop();
        }
    }
}
