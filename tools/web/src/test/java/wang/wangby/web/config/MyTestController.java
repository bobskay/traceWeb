package wang.wangby.web.config;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.web.controller.BaseController;

@RestController
@RequestMapping("test")
public class MyTestController extends BaseController {

    @RequestMapping("hello")
    public String hello(){
        return "hello world";
    }
}
