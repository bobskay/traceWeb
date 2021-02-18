package wang.wangby.trace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.web.Menu;
import wang.wangby.entity.request.Response;
import wang.wangby.trace.dto.StockOrderDto;
import wang.wangby.trace.model.StockOrder;
import wang.wangby.trace.service.MarketService;
import wang.wangby.trace.utils.TimeUtil;
import wang.wangby.utils.DateTime;
import wang.wangby.utils.StringUtil;
import wang.wangby.web.controller.BaseController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("stockOrder")
public class StockOrderController extends BaseController {

    @Autowired
    MarketService marketService;

    @Menu("历史记录")
    @RequestMapping("index")
    public String index() {
        Map map=new HashMap<>();
        map.put("today", DateTime.current().toString(DateTime.Format.YEAR_TO_DAY));
        return $("index",map);
    }

    @RequestMapping("/query")
    public Response<List<StockOrder>> query(StockOrderDto order)  {
        if(StringUtil.isEmpty(order.getDate())){
            order.setDate(TimeUtil.today());
        }

        List<StockOrder> list= marketService.query(order);
        Collections.sort(list,(o1, o2) -> {
            if(o1.getCreatedAt()==null){
                return  -1;
            }
            if(o2.getCreatedAt()==null){
                return 1;
            }

            return o2.getCreatedAt().compareTo(o1.getCreatedAt());
        });
        return respone(list);
    }

    @RequestMapping("/deleteById")
    @Remark("删除")
    public Response<Integer> deleteById(Long[] id) throws Exception {
        for(long i:id){
            marketService.deleteOrder(i);
        }
        return respone(id.length);
    }
}
