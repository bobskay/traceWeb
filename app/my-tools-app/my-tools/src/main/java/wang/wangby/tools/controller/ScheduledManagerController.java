package wang.wangby.tools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.web.Menu;
import wang.wangby.config.Beans;
import wang.wangby.entity.request.Response;
import wang.wangby.tools.thread.job.JobInfo;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("scheduledManager")
public class ScheduledManagerController  extends ToolController {

    @Autowired
    Beans beans;

    @RequestMapping("/index")
    @Menu("计划任务管理")
    public String index( ) {
        List<JobInfo> jobs=new ArrayList<>();
        beans.iteratorScheduledMap(se->{
            jobs.add(se.getJobInfo());
        });
        return $("index",jobs);
    }


    @RequestMapping("/pause")
    public Response<String> pause(String name) {
        beans.iteratorScheduledMap(se->{
            if(se.getJobInfo().getName().equalsIgnoreCase(name)){
                se.pause();
            }
        });
        return respone("ok");
    }

    @RequestMapping("/resume")
    public Response<String> resume(String name) {
        beans.iteratorScheduledMap(se->{
            if(se.getJobInfo().getName().equalsIgnoreCase(name)){
                se.resume();
            }
        });
        return respone("ok");
    }

    public int order(){
        return 99;
    }
}
