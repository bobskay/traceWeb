package wang.wangby.trace.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.web.Menu;
import wang.wangby.entity.request.Response;
import wang.wangby.trace.dto.ProfitDto;
import wang.wangby.trace.dto.TraceOrderDto;
import wang.wangby.trace.model.Profit;
import wang.wangby.trace.model.TraceOrder;
import wang.wangby.trace.service.ProfitService;
import wang.wangby.trace.service.TraceOrderService;
import wang.wangby.trace.utils.TimeUtil;
import wang.wangby.trace.vo.ProfitVo;
import wang.wangby.utils.DateTime;
import wang.wangby.utils.StringUtil;
import wang.wangby.web.controller.BaseController;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("profit")
public class ProfitController extends BaseController {

    @Autowired
    ProfitService profitService;

    @Menu("盈亏汇总")
    @RequestMapping("index")
    public String index() {
        Map map = new HashMap<>();
        map.put("start", DateTime.current().toString(DateTime.Format.YEAR_TO_DAY) + " 00:00:00");
        map.put("end", DateTime.current().toString(DateTime.Format.YEAR_TO_DAY)+" 23:59:59");
        return $("index", map);
    }

    @RequestMapping("data")
    public String data(ProfitDto query) {
        if(query.getStart()==null){
            query.setStart(DateTime.current(DateTime.Format.YEAR_TO_DAY));
        }
        if(query.getEnd()==null){
            query.setEnd(DateTime.current(DateTime.Format.YEAR_TO_DAY).addDay(1).addSecond(-1));
        }

        ProfitVo vo=new ProfitVo();
        for(Profit profit:profitService.query(query)){
            vo.addProfit(profit);
        }
        return json(vo);
    }

}
