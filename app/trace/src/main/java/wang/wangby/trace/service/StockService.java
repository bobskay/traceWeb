package wang.wangby.trace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.dto.Account;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.trace.model.Stock;

import java.util.Collections;
import java.util.List;

@Service
public class StockService implements CommandLineRunner {

    @Autowired
    Exchange exchange;

    private Stock stock;

    public Stock getStock(){
        return stock;
    }

    @Scheduled(cron = "0/10 * * * * ? ")
    public void updateSock(){
        List<OpenOrder> list = exchange.openOrders();
        Collections.sort(list, (o1, o2) -> {
            return o1.getClientOrderId().compareTo(o2.getClientOrderId());
        });

        int hold = 0;
        Account account = exchange.account();
        for (Account.PositionsDTO positionsDTO : account.getPositions()) {
            if (positionsDTO.getSymbol().equalsIgnoreCase(exchange.getSymbol())) {
                //持有
                hold = positionsDTO.getPositionAmt().intValue();
                break;
            }
        }
        stock=new Stock();
        stock.setHolds(hold);
        stock.setOpenOrders(list);
    }

    @Override
    public void run(String... args) throws Exception {
        updateSock();
    }
}
