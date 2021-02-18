package wang.wangby.tools.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.Remark;
import wang.wangby.utils.DateTime;
import wang.wangby.utils.FixSizeList;
import wang.wangby.tools.controller.vo.AlterMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alertReceiver")
public class AlertReceiverController extends ToolController {

    FixSizeList<AlterMessage> list=new FixSizeList(1000);

    @Remark("添加报警信息")
    @RequestMapping("insert")
    public String insert(@RequestBody String msg){
        AlterMessage am=new AlterMessage();
        am.setReceiveTime(DateTime.current());
        try{
            Map map=jsonUtil.toBean(msg, HashMap.class);
            am.setMessage(map);
        }catch (Exception ex){
            log.info("收到的报警信息不是json:"+msg);
            am.setMessage(msg);
        }
        list.add(am);
        return "ok";
    }

    @Remark("显示存储的信息")
    @RequestMapping("print")
    public List print(){
        return list.getData();
    }
}
