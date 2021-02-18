package wang.wangby.testcase.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.web.Menu;
import wang.wangby.entity.request.Response;
import wang.wangby.testcase.model.JvmInfo;
import wang.wangby.testcase.service.JvmService;
import wang.wangby.web.controller.BaseController;

import java.util.*;

@RequestMapping("testCase/gcTest")
@RestController
@Slf4j
@Menu("测试")
public class GcTestController extends BaseController {
    private volatile List<Byte[]> bigData = Collections.synchronizedList(new ArrayList<>());//常驻内存的大对象

    @Menu("内存测试")
    @RequestMapping("memory")
    public String memory() {
        Map map = new HashMap<>();
        map.put("currentSize", bigData.size());
        map.put("jvmInfo", new JvmService().getMemroy());
        return $("memory", map);
    }

    //内存测试,创建一个大对象
    @RequestMapping("createData")
    public Response<Integer> createData(Integer size) {
        byte[] data = new byte[size * 1024];
        log.debug("使用内存:" + size + "K");
        return Response.success(size);
    }

    //往内存添加数据
    @RequestMapping("addData")
    public Response<Integer> addData(Integer size) {
        synchronized (bigData) {
            for (int i = 0; i < size; i++) {
                bigData.add(new Byte[1024]);
            }
        }
        return respone(bigData.size());
    }

    @RequestMapping("clearData")
    public Response<Integer> clearData(Integer size) {
        bigData = new ArrayList<>();
        return respone(0);
    }

    //手动调用gc
    @RequestMapping("gc")
    public Response<String> gc() {
        System.gc();
        return respone("ok");
    }

    @RequestMapping("getJvm")
    public Response<JvmInfo> getJvm() {
        return respone(new JvmService().getMemroy());
    }

    @RequestMapping("自定义类加载")
    public void loadClass(int count) {

    }
}
