package wang.wangby.tools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.web.Menu;
import wang.wangby.entity.request.Response;
import wang.wangby.tools.controller.vo.ShellInfo;
import wang.wangby.tools.shell.LongTimeShellPool;
import wang.wangby.tools.shell.ShellClient;
import wang.wangby.tools.shell.ShellExecInfo;
import wang.wangby.utils.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@RequestMapping("shell")
@RestController
public class ShellController extends ToolController {
    private Map<String, BlockingQueue> map;

    @Autowired
    LongTimeShellPool longTimeShellPool;
    @Autowired
    ShellClient shellClient;

    @Menu("执行shell")
    @RequestMapping("index")
    public String index() {
        return $("index");
    }

    @RequestMapping("exec")
    public Response<Map> exec(@RequestBody  ShellInfo shellInfo) throws Exception {
        if(StringUtil.isNotEmpty(shellInfo.getKey())){
            longTimeShellPool.kill(shellInfo.getKey());
        }
        ShellExecInfo info= longTimeShellPool.addShell(shellInfo.getCommand(),shellInfo.getHostname(),shellInfo.getUsername(),shellInfo.getPassword());
        Map<String,Object> map=new HashMap<>();
        if(StringUtil.isNotEmpty(info.getKey())){
            map.put("key",info.getKey());
        }else{
            map.put("datas",info.getDatas());
        }
       return respone(map);
    }

    @RequestMapping("read")
    public Response<List<String>> read(String key){
        List<String> data=longTimeShellPool.poll(key);
        if(data==null){
            return Response.fail("shell已经关闭");
        }
        return respone(data);
    }

}
