package wang.wangby.tools.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.web.Menu;
import wang.wangby.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("threadInfo")
@RestController
public class ThreadInfoController extends ToolController {

    @RequestMapping("index")
    @Menu("线程信息")
    public String index() {
        System.gc();
        List<Thread> threads = new ArrayList<>();

        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            Thread thread = entry.getKey();
            threads.add(thread);
        }
        Map map = new HashMap();
        CollectionUtil.sort(threads,Thread::getName);
        map.put("threadList", threads);
        return $("index", map);
    }

    @RequestMapping("stackTraces")
    public String stackTraces(String name) {
        Map map=new HashMap();
        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            if (entry.getKey().getName().equals(name)) {
                map.put("stackTraceList",entry.getValue());
                return $("stackTraces",map);
            }
        }
        return $("stackTraces",map);
    }
}
