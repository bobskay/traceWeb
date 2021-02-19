package wang.wangby.trace.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.web.Menu;
import wang.wangby.entity.request.Response;
import wang.wangby.utils.FileUtil;
import wang.wangby.utils.IdWorker;
import wang.wangby.web.controller.BaseController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("test")
public class TestController extends BaseController {

    @RequestMapping("index")
    public String index() {
        return $("index");
    }

    @RequestMapping("save")
    public String save(String text) throws IOException {
        String uuid= IdWorker.nextString();
        String file = "/opt/file/" + uuid;
        FileUtil.createFile(file, text);
        return json(Response.success(uuid));
    }
}
