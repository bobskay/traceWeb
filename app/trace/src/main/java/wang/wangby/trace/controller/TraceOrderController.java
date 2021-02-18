package wang.wangby.trace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.web.Menu;
import wang.wangby.entity.request.Response;
import wang.wangby.trace.dto.TraceOrderDto;
import wang.wangby.trace.model.TraceOrder;
import wang.wangby.trace.service.TraceOrderService;
import wang.wangby.utils.DateTime;
import wang.wangby.utils.StringUtil;
import wang.wangby.web.controller.BaseController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("traceOrder")
public class TraceOrderController extends BaseController {


    @Autowired
    TraceOrderService traceOrderService;

    @Menu("完成订单")
    @RequestMapping("index")
    public String index() {
        Map map=new HashMap<>();
        map.put("today", DateTime.current().toString(DateTime.Format.YEAR_TO_DAY));
        return $("index",map);
    }

    @RequestMapping("/query")
    public Response<List<TraceOrder>> query(TraceOrderDto order)  {
        if(StringUtil.isEmpty(order.getDate())){
            order.setDate(DateTime.today());
        }

        List<TraceOrder> list= traceOrderService.query(order);
        Collections.sort(list,((o1, o2) -> {
            if(o1.getFinishAt()==null){
                return -1;
            }
            if(o2.getFinishAt()==null){
                return 1;
            }
            return o2.getFinishAt().compareTo(o1.getFinishAt());
        }));
        return respone(list);
    }

    @RequestMapping("/deleteById")
    @Remark("删除")
    public Response<Integer> deleteById(Long[] id) throws Exception {
        for(long i:id){
            traceOrderService.deleteById(i);
        }
        return respone(id.length);
    }
}
