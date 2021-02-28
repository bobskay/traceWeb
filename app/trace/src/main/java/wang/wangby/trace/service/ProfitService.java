package wang.wangby.trace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.wangby.repostory.Repository;
import wang.wangby.trace.dto.ProfitDto;
import wang.wangby.trace.dto.TraceOrderDto;
import wang.wangby.trace.model.Profit;
import wang.wangby.trace.model.TraceOrder;
import wang.wangby.utils.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProfitService {


    @Autowired
    Repository repository;

    public List<Profit> query(ProfitDto query) {
       return query(query.getStat(),query.getEnd());
    }

    public void deleteById(long i) throws Exception {
        repository.delete(Profit.class, i);
    }

    public List<Profit> query(Date start, Date end) {
        List<Profit> all = repository.select(new Profit(), 0, 10000);
        List<Profit> list = new ArrayList<>();
        for (Profit o : all) {
            long time = o.getDate().getTime();
            if (time > start.getTime() && time <= end.getTime()) {
                list.add(o);
            }
        }
        return list;
    }

    public Profit getByTime(DateTime end) {
        List<Profit> all = repository.select(new Profit(), 0, 10000);
        for(Profit p:all){
            if(p.getDate().getTime()==end.getTime()){
                return p;
            }
        }
        return  null;
    }
}


