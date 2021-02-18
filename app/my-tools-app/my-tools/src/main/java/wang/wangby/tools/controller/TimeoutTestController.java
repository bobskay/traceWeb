package wang.wangby.tools.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.entity.request.Response;

@RestController
@RequestMapping("timeoutTest")
public class TimeoutTestController extends ToolController {

    @RequestMapping("/sleep")
    public Response<String> sleep(int millis) throws InterruptedException {
        Thread.sleep(millis);
        return Response.success("ok");
    }

    public Response<String> sleepFallback(int millis) throws InterruptedException {
        return Response.success("sleep超时");
    }

}
