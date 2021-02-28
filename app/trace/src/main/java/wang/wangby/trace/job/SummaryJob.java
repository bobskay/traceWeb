package wang.wangby.trace.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.dto.Account;
import wang.wangby.repostory.Repository;
import wang.wangby.serialize.json.JsonUtil;
import wang.wangby.trace.model.Profit;
import wang.wangby.trace.model.TraceOrder;
import wang.wangby.trace.service.MarketService;
import wang.wangby.trace.service.ProfitService;
import wang.wangby.trace.service.TraceOrderService;
import wang.wangby.utils.DateTime;

import java.math.BigDecimal;
import java.util.List;

@Component
public class SummaryJob {

    @Autowired
    Exchange exchange;
    @Autowired
    MarketService marketService;
    @Autowired
    TraceOrderService traceOrderService;
    @Autowired
    ProfitService profitService;
    @Autowired
    Repository repository;

    @Scheduled(cron = "0 * * * * ?")
    public void addProfit() throws Exception {
        DateTime start = DateTime.current(DateTime.Format.YEAR_TO_HOUR);
        DateTime end = DateTime.current();

         Profit profit = profitService.getByTime(start);
         if(profit==null){
             profit=new Profit();
             profit.setDate(start);
             repository.insert(profit);
         }

        Account account = exchange.account();

        profit.setPrice(marketService.getPrice());
        profit.setAccount(account.getTotalMarginBalance());

        List<TraceOrder> traceOrderList = traceOrderService.query(start, end);
        profit.setExchangeCount(traceOrderList.size());
        BigDecimal quantity = BigDecimal.ZERO;
        BigDecimal amount = BigDecimal.ZERO;
        for (TraceOrder o : traceOrderList) {
            quantity = quantity.add(o.getQuantity());
            BigDecimal currentAmount=o.getSell().subtract(o.getBuy()).multiply(o.getQuantity());
            amount = amount.add(currentAmount);
        }
        profit.setExchangeQuantity(quantity);
        profit.setProfitAmount(amount);
        repository.update(profit);
    }
}


