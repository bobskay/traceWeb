package wang.wangby.monitor.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import wang.wangby.test.TestBase;
import wang.wangby.testcase.model.vo.NotifyMessage;
import wang.wangby.testcase.service.DingdingNotifyService;
import wang.wangby.tools.http.HttpConfig;
import wang.wangby.tools.http.HttpUtil;
import wang.wangby.tools.http.MyHttpClient;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class DingdingNotifyServiceTest extends TestBase {

    @Test
    public void send() throws IOException {
        MyHttpClient client = HttpUtil.createClient(new HttpConfig(), null, null);
        String url = "https://oapi.dingtalk.com/robot/send?access_token=d187a932e529602eaccad2fb351e86f8acc61b1089b27864d9a5e2eb8c49e713";
        DingdingNotifyService service = new DingdingNotifyService(jsonUtil, client, url);
        NotifyMessage msg = new NotifyMessage();
        msg.setTarget(new ArrayList<>());
        msg.getTarget().add("1362709305");
        msg.setMessage("今天的处理结果 ");
        String result = service.send(msg);
        log.debug("返回结果:" + result);

    }
}