package wang.wangby.trace.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.response.Order;
import wang.wangby.serialize.json.JsonUtil;
import wang.wangby.trace.config.OrderConfig;
import wang.wangby.trace.dao.TraceOrderDao;
import wang.wangby.trace.dto.TraceOrderDto;
import wang.wangby.trace.model.MyOrder;
import wang.wangby.trace.model.TraceOrder;
import wang.wangby.trace.model.UpInfo;
import wang.wangby.trace.utils.BillNo;
import wang.wangby.utils.IdWorker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MyOrderService {

    @Autowired
    Exchange exchange;

    @Autowired
    OrderConfig orderConfig;

    @Autowired
    JsonUtil jsonUtil;

    @Autowired
    TraceOrderDao traceOrderDao;

    public void finish(Order order) {
        boolean isClose = BillNo.isClose(order.getClientOrderId());
        if (!isClose) {
            return;
        }
        String billNo = order.getClientOrderId();
        TraceOrder traceOrder = new TraceOrder();
        traceOrder.setSellOrderId(billNo);
        traceOrder.setCreatedAt(new Date());
        traceOrder.setFinishAt(new Date());
        traceOrder.setBuyFinish(new Date());
        traceOrder.setQuantity(order.getOrigQty());
        traceOrder.setId(IdWorker.nextLong());
        if (BillNo.isBuy(billNo)) {
            traceOrder.setSell(order.getAvgPrice());
            traceOrder.setBuy(BillNo.getPrice(billNo));
        } else {
            traceOrder.setSell(BillNo.getPrice(billNo));
            traceOrder.setBuy(order.getAvgPrice());
        }
        traceOrderDao.insert(traceOrder);
    }

    public void newBuy() throws InterruptedException {
        String billNo = BillNo.newBuy();
        exchange.buy(billNo, BigDecimal.ZERO, orderConfig.getOpenQuantity());
        for (int i = 0; i < 20; i++) {
            Thread.sleep(100);
            Order buyOrder = exchange.getOrder(billNo);
            if (buyOrder.isFinish()) {
                String sellNo = BillNo.close(billNo, buyOrder.getAvgPrice());
                BigDecimal sellPrice = buyOrder.getAvgPrice().subtract(orderConfig.getUpgradePrice());
                exchange.preSell(sellNo, sellPrice, orderConfig.getOpenQuantity());
                return;
            }
        }

    }

    public void newSell() throws InterruptedException {
        String billNo = BillNo.newSell();
        exchange.sell(billNo, BigDecimal.ZERO, orderConfig.getOpenQuantity());
        for (int i = 0; i < 20; i++) {
            Thread.sleep(100);
            Order sellOrder = exchange.getOrder(billNo);
            if (sellOrder.isFinish()) {
                String buyNo = BillNo.close(billNo, sellOrder.getAvgPrice());
                BigDecimal buyPrice = sellOrder.getAvgPrice().add(orderConfig.getUpgradePrice());
                exchange.preBuy(buyNo, buyPrice, orderConfig.getOpenQuantity());
                return;
            }
        }
        log.error("订单长时间未成交，直接取消：" + billNo);
        exchange.cancel(billNo);
    }

    public List<MyOrder> openOrders() {
        List<MyOrder> myOrders = new ArrayList<>();
        List<OpenOrder> orders = exchange.openOrders();
        for (OpenOrder openOrder : orders) {
            MyOrder order = toMyOrder(openOrder);
            myOrders.add(order);
        }
        return myOrders;
    }

    private MyOrder toMyOrder(OpenOrder openOrder) {
        MyOrder myOrder = new MyOrder();
        myOrder.setBillNo(openOrder.getClientOrderId());
        myOrder.setCreatedAt(new Date(openOrder.getTime()));
        myOrder.setQuantity(openOrder.getOrigQty());
        if (myOrder.isBuy()) {
            myOrder.setBuyPrice(BillNo.getPrice(openOrder.getClientOrderId()));
            myOrder.setSellPrice(openOrder.getStopPrice());
        } else {
            myOrder.setSellPrice(BillNo.getPrice(openOrder.getClientOrderId()));
            myOrder.setBuyPrice(openOrder.getStopPrice());
        }
        return myOrder;
    }

    public boolean checkCreate(MyOrder order, BigDecimal price) throws InterruptedException {
        UpInfo upInfo = order.getUpInfo(price, orderConfig.getUpgradePrice(), orderConfig.getStep());
        if (!upInfo.isUp()) {
            return false;
        }
        if (order.isBuy()) {
            reSell(order, upInfo.getReOrderPrice());
        } else {
            reBuy(order, upInfo.getReOrderPrice());
        }
        return true;
    }

    public void newOrder(MyOrder order) throws InterruptedException {
        if (order.isBuy()) {
            newBuy();
        } else {
            newSell();
        }
    }

    public void checkUpgrade(MyOrder order, BigDecimal price) throws InterruptedException {
        UpInfo upInfo = order.getUpInfo(price, orderConfig.getUpgradePrice(), orderConfig.getStep());
        if (!upInfo.isUp()) {
            return;
        }
        if (order.isBuy()) {
            reSell(order, upInfo.getReOrderPrice());
        } else {
            reBuy(order, upInfo.getReOrderPrice());
        }
    }

    private String reBuy(MyOrder order, BigDecimal newPrice) throws InterruptedException {
        return exchange.reBuy(order.getBillNo(), newPrice, order.getQuantity());
    }

    private String reSell(MyOrder order, BigDecimal newPrice) throws InterruptedException {
        return exchange.reSell(order.getBillNo(), newPrice, order.getQuantity());
    }


}