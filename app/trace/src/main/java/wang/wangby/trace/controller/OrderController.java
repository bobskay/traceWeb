package wang.wangby.trace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.web.Menu;
import wang.wangby.entity.request.Response;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.enums.OrderSide;
import wang.wangby.exchange.socket.listener.AccountUpdateListener;
import wang.wangby.exchange.vo.BalanceVo;
import wang.wangby.exchange.vo.MyOrderVo;
import wang.wangby.trace.config.MarketConfig;
import wang.wangby.trace.config.OrderConfig;
import wang.wangby.trace.config.Rule;
import wang.wangby.trace.config.RunningInfo;
import wang.wangby.trace.model.MyOrder;
import wang.wangby.trace.model.Stock;
import wang.wangby.trace.service.MarketService;
import wang.wangby.trace.service.MyOrderService;
import wang.wangby.trace.service.StockService;
import wang.wangby.trace.vo.TraceVo;
import wang.wangby.web.controller.BaseController;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("order")
public class OrderController extends BaseController {

    @Autowired
    StockService stockService;
    @Autowired
    Exchange exchange;
    @Autowired
    Rule rule;
    @Autowired
    AccountUpdateListener accountUpdateListener;
    @Autowired
    MarketConfig marketConfig;
    @Autowired
    MyOrderService myOrderService;
    @Autowired
    OrderConfig orderConfig;
    @Autowired
    MarketService marketService;
    @Autowired
    RunningInfo runningInfo;

    @Menu("挂单")
    @RequestMapping("index")
    public String index() {
        List<MyOrder> orders = myOrderService.openOrders();
        Map map = new HashMap<>();
        Collections.sort(orders,((o1, o2) -> {
            return o1.getBuyPrice().compareTo(o2.getBuyPrice());
        }));

        List<MyOrderVo> openOrders=new ArrayList<>();
        for(MyOrder order:orders){
            MyOrderVo vo=new MyOrderVo(order,orderConfig,marketService.getPrice());
            openOrders.add(vo);
        }

        TraceVo traceVo=getTraceVo();
        BigDecimal profit=new BigDecimal(0);
        for(MyOrder o:orders){
            profit=profit.add(o.getProfit());
        }
        traceVo.setProfit(profit);
        map.put("openOrders", openOrders);
        map.put("traceVo",traceVo );
        return $("index", map);
    }

    @RequestMapping("cancel")
    public String cancel(String orderId) {
        exchange.cancel(orderId);
        return json(Response.success(""));
    }

    @RequestMapping("trace")
    public String trace() {
        TraceVo tr = getTraceVo();
        return json(Response.success(tr));
    }

    private TraceVo getTraceVo() {
        Stock stock = stockService.getStock();
        TraceVo tr = new TraceVo();
        tr.setPrice(marketService.getPrice());
        tr.setHold(stock.getHolds() + "");
        tr.setBase(runningInfo.getBasePrice());
        if(tr.getBase()==null){
            tr.setBase(tr.getPrice());
        }
        if(tr.getBase()==null){
            return tr;
        }
        BigDecimal buy=tr.getBase().add(orderConfig.getStep().multiply(new BigDecimal(2)));
        tr.setBuy(buy+"("+buy.subtract(tr.getPrice())+")");
        BigDecimal sell=tr.getBase().subtract(orderConfig.getStep().multiply(new BigDecimal(2)));
        tr.setSell(sell+"("+sell.subtract(tr.getPrice())+")");
        BalanceVo vo = accountUpdateListener.getBalance(marketConfig.getAccountSymbol());
        if (vo != null) {
            tr.setWallet(vo.getTotal() + "");
        }else{
            tr.setWallet("未初始化");
        }
        return tr;
    }
}
