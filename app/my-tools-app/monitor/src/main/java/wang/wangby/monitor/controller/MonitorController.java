package wang.wangby.monitor.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.web.Menu;
import wang.wangby.config.Beans;
import wang.wangby.web.controller.BaseController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("monitor")
@Menu("监控")
public class MonitorController extends BaseController implements MeterBinder {

    @Autowired
    Beans beans;
    Counter counter;

    @Menu("监控项管理")
    @RequestMapping("index")
    public String index() {
        counter.increment();
        Map map = new HashMap<>();
        List list = new ArrayList<>();
        beans.iteratorMonitorData(monitorData -> {
            list.add(monitorData);
        });
        map.put("monitorDataList", list);
        return $("index", map);
    }

    @Menu("actuator")
    @RequestMapping("actuator")
    public String actuator() {
        return $("actuator");
    }



    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        counter = meterRegistry.counter("myCount", "tagA", "blog", "tagB", "visit");
    }
}
