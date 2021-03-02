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
import java.util.Date;
import java.util.List;

@Component
public class SummaryJob {

    @Autowired
    Exchange exchange;
    @Autowired
    MarketService marketService;
    @Autowired
    ProfitService profitService;
    @Autowired
    Repository repository;

  //  @Scheduled(cron = "0 0/10 * * * ?")
  @Scheduled(cron = "0 0/1 * * * ?")
    public void addProfit() throws Exception {
         Profit profit = new Profit();
        Account account = exchange.account();
        profit.setPrice(marketService.getPrice());
        profit.setAccount(account.getTotalMarginBalance());
        profit.setDate(new Date());
        repository.insert(profit);
    }
}


