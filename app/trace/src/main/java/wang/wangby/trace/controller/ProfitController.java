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

    @RequestMapping("/query")
    public Response<List<Profit>> query(ProfitDto query) {
        if (StringUtil.isEmpty(query.getStat())) {
            query.setStat(DateTime.current(DateTime.Format.YEAR_TO_DAY));
        }
        if (StringUtil.isEmpty(query.getEnd())) {
            query.setEnd(DateTime.current());
        }

        List<Profit> queryResult = profitService.query(query);
        Collections.sort(queryResult,((o1, o2) -> {
            return o2.getDate().compareTo(o1.getDate());
        }));

        if("day".equalsIgnoreCase(query.getType())){
            queryResult=groupByDay(queryResult);
        }
        Collections.sort(queryResult,((o1, o2) -> {
            return o2.getDate().compareTo(o1.getDate());
        }));

        return respone(queryResult);
    }

    //按天汇总
    private List<Profit> groupByDay( List<Profit> profits) {
        Map<String,Profit> map=new HashMap<>();
        for(Profit p:profits){
            String day=new DateTime(p.getDate(), DateTime.Format.YEAR_TO_DAY).toString();
            Profit temp=map.get(day);
            if(temp==null){
                temp=new Profit();
                temp.setDate(new DateTime(day));
                temp.setProfitAmount(BigDecimal.ZERO);
                temp.setExchangeQuantity(BigDecimal.ZERO);
                temp.setExchangeCount(0);
                temp.setPrice(p.getPrice());
                temp.setAccount(p.getAccount());
                map.put(day,temp);
            }
            temp.setProfitAmount(temp.getProfitAmount().add(p.getProfitAmount()));
            temp.setExchangeQuantity(temp.getExchangeQuantity().add(p.getExchangeQuantity()));
            temp.setExchangeCount(temp.getExchangeCount()+p.getExchangeCount());
        };
        return new ArrayList<>(map.values());
    }

    @RequestMapping("/deleteById")
    @Remark("删除")
    public Response<Integer> deleteById(Long[] id) throws Exception {
        for (long i : id) {
            profitService.deleteById(i);
        }
        return respone(id.length);
    }
}
