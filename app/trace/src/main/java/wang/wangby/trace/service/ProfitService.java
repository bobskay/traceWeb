package wang.wangby.trace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.wangby.repostory.Repository;
import wang.wangby.trace.dto.ProfitDto;
import wang.wangby.trace.dto.TraceOrderDto;
import wang.wangby.trace.model.Profit;
import wang.wangby.trace.model.StockOrder;
import wang.wangby.trace.model.TraceOrder;
import wang.wangby.utils.DateTime;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ProfitService {


    @Autowired
    Repository repository;
    @Autowired
    TraceOrderService traceOrderService;

    public List<Profit> query(ProfitDto query) {
        return query(query.getStart(), query.getEnd(),query.getType());
    }

    public void deleteById(long i) throws Exception {
        repository.delete(Profit.class, i);
    }

    public List<Profit> query(Date start, Date end,String type) {
        List<Profit> all = repository.select(new Profit(), 0, 10000);
        List<Profit> list = new ArrayList<>();

        for (Profit o : all) {
            if("day".equals(type)){
                String day=new DateTime(o.getDate(), DateTime.Format.YEAR_TO_DAY).toString(DateTime.Format.YEAR_TO_SECOND);
                String hour=new DateTime(o.getDate(), DateTime.Format.YEAR_TO_HOUR).toString(DateTime.Format.YEAR_TO_SECOND);
                if(!day.equals(hour)){
                    continue;
                }
            }

            if("hour".equals(type)){
                String mint=new DateTime(o.getDate(), DateTime.Format.YEAR_TO_MINUTE).toString(DateTime.Format.YEAR_TO_SECOND);
                String hour=new DateTime(o.getDate(), DateTime.Format.YEAR_TO_HOUR).toString(DateTime.Format.YEAR_TO_SECOND);
                if(!mint.equals(hour)){
                    continue;
                }
            }

            long time = o.getDate().getTime();
            if (time > start.getTime() && time <= end.getTime()) {
                list.add(o);
            }
        }
        if(list.size()==0){
            return list;
        }

        Collections.sort(list, (o1, o2) -> {
            return o1.getDate().compareTo(o2.getDate());
        });

        List<TraceOrder> orders = traceOrderService.query(start, end);
        Collections.sort(orders, ((o1, o2) -> {
            return o1.getFinishAt().compareTo(o2.getFinishAt());
        }));


        Date last=all.get(0).getDate();
        for (Profit po : list) {
            int count=0;
            BigDecimal proAmount=BigDecimal.ZERO;
            BigDecimal excAmount=BigDecimal.ZERO;
            for (TraceOrder to : orders) {
                if(to.getFinishAt().compareTo(last)<0){
                    continue;
                }

                if(to.getFinishAt().compareTo(po.getDate())<=0){
                    count++;
                    excAmount=excAmount.add(to.getQuantity());
                    BigDecimal profit=to.getQuantity().multiply(to.getSell().subtract(to.getBuy()));
                    proAmount=proAmount.add(profit);
                }
            }
            po.setExchangeCount(count);
            po.setProfitAmount(proAmount);
            po.setExchangeQuantity(excAmount);
            last=po.getDate();
        }

        return list;
    }
}


