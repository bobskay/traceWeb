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
import wang.wangby.exchange.socket.listener.AggTradeListener;
import wang.wangby.exchange.vo.BalanceVo;
import wang.wangby.trace.config.MarketConfig;
import wang.wangby.trace.config.Rule;
import wang.wangby.trace.model.Stock;
import wang.wangby.trace.service.MarketService;
import wang.wangby.trace.service.StockService;
import wang.wangby.trace.utils.OrderId;
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
    MarketService marketService;
    @Autowired
    Exchange exchange;
    @Autowired
    Rule rule;
    @Autowired
    AccountUpdateListener accountUpdateListener;
    @Autowired
    MarketConfig marketConfig;

    @Autowired
    AggTradeListener aggTradeListener;

    @Menu("挂单")
    @RequestMapping("index")
    public String index() {
        Stock stock = stockService.getStock();
        Map map = new HashMap<>();
        List<OpenOrder> opens=stock.getOpenOrders();
        Collections.sort(opens,((o1, o2) -> {
            return o1.getPrice().compareTo(o2.getPrice());
        }));
        map.put("openOrders", opens);
        map.put("traceVo", getTraceVo());
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
        tr.setPrice(marketService.getPrice() + "");
        tr.setHold(stock.getHolds() + "");
        tr.setHigh(rule.stopPrice() + "");

        if(stock.sellPrice()!=null){
            tr.setSell(stock.sellPrice().getPrice()+"");
        }

        BigDecimal buy=aggTradeListener.getRecentHigh().subtract(marketConfig.getBuySubtract());
        tr.setBuy(buy+"");

        BalanceVo vo = accountUpdateListener.getBalance(marketConfig.getAccountSymbol());
        if (vo != null) {
            tr.setWallet(vo.getTotal() + "");
        }else{
            tr.setWallet("未初始化");
        }
        return tr;
    }
}
