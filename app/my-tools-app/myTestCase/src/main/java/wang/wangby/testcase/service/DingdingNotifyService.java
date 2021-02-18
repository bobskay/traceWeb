package wang.wangby.testcase.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpUriRequest;
import wang.wangby.serialize.json.JsonUtil;
import wang.wangby.testcase.model.vo.NotifyMessage;
import wang.wangby.tools.http.HttpRequestMethod;
import wang.wangby.tools.http.MyHttpClient;

import java.io.IOException;
import java.util.List;

@Slf4j
public class DingdingNotifyService {

    JsonUtil jsonUtil;
    MyHttpClient httpClient;
    String sendUrl;

    public DingdingNotifyService(JsonUtil jsonUtil,MyHttpClient httpClient,String sendUrl){
        this.jsonUtil=jsonUtil;
        this.httpClient=httpClient;
        this.sendUrl=sendUrl;
    }

    public  String send(NotifyMessage notifyMessage) throws IOException {
        DingdingMessage msg=new DingdingMessage();
        msg.setMsgtype("text");
        msg.getText().setContent(notifyMessage.getMessage());
        msg.getAt().setAtMobiles(notifyMessage.getTarget());
        String body=jsonUtil.toString(msg);
        log.debug("准备往钉钉发送消息:{}",body);
        HttpUriRequest request=httpClient.createRequest(sendUrl, HttpRequestMethod.POST,body);
        return httpClient.getString(request);
    }

    @Data
    public class DingdingMessage{
        String msgtype;
        Text text=new Text();
        At at=new At();

        @Data
        public class Text{
            String content;
        }

        @Data
        public class At{
            List<String> atMobiles;
            Boolean isAtAll=false;
        }
    }
}
