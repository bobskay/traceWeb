package wang.wangby.trace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.dto.Account;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.trace.model.Stock;
import wang.wangby.trace.utils.BillNo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class StockService implements CommandLineRunner {

    @Autowired
    Exchange exchange;

    private volatile Stock stock;


    public Stock getStock(){
        if(stock==null){
            synchronized (this){
                updateSock();
            }
        }
        return stock;
    }

    public Stock updateSock(){
        List<OpenOrder> all = exchange.openOrders();
        List<OpenOrder> list=new ArrayList<>();
        for(OpenOrder o:all){
            if(BillNo.getOrderSide(o.getClientOrderId())!=null){
                list.add(o);
            }
        }

        Collections.sort(list, Comparator.comparing(OpenOrder::getClientOrderId));

        BigDecimal hold = BigDecimal.ZERO;
        Account account = exchange.account();
        for (Account.PositionsDTO positionsDTO : account.getPositions()) {
            if (positionsDTO.getSymbol().equalsIgnoreCase(exchange.getSymbol())) {
                //持有
                hold= positionsDTO.getPositionAmt();
                break;
            }
        }
        stock=new Stock();
        stock.setHolds(hold);
        stock.setOpenOrders(list);
        return stock;
    }

    @Override
    public void run(String... args) throws Exception {
        updateSock();
    }



}
