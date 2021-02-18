package wang.wangby.trace.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.web.Menu;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.dto.Kline;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.enums.CandlestickInterval;
import wang.wangby.exchange.socket.listener.AggTradeListener;
import wang.wangby.trace.config.MarketConfig;
import wang.wangby.trace.model.Stock;
import wang.wangby.trace.service.KlineService;
import wang.wangby.trace.service.MarketService;
import wang.wangby.trace.service.StockService;
import wang.wangby.trace.vo.KlineVo;
import wang.wangby.utils.DateTime;
import wang.wangby.utils.StringUtil;
import wang.wangby.web.controller.BaseController;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("trace")
public class TraceController extends BaseController {

    @Autowired
    MarketService marketService;
    @Autowired
    Exchange exchange;
    @Autowired
    MarketConfig marketConfig;
    @Autowired
    KlineService klineService;
    @Autowired
    StockService stockService;

    @Menu("交易信息")
    @RequestMapping("index")
    public String index() {
        Map map = new HashMap<>();
        map.put("price", marketService.getPrice());
        map.put("marketConfig", marketConfig);
        return $("index", map);
    }


    @RequestMapping("kline")
    public String kline(Double zoomStart, String type) {
        if (zoomStart == null) {
            zoomStart = 80D;
        }
        if (StringUtil.isEmpty(type)) {
            type = CandlestickInterval.ONE_MINUTE.code;
        }


        Stock stock = stockService.getStock();
        KlineVo klineVo = new KlineVo();
        klineVo.setCurrent(marketService.getPrice() + "");
        if (stock.buyPrice() != null) {
            klineVo.setBuy(stock.buyPrice().getPrice() + "");
        }
        if (stock.sellPrice() != null) {
            klineVo.setSell(stock.sellPrice().getPrice() + "");
        }

        StringBuilder sellDetail = new StringBuilder();
        List<OpenOrder> sells=new ArrayList<>(stock.sells());
        Collections.sort(sells,(Comparator.comparing(OpenOrder::getPrice)));
        for (OpenOrder op : sells) {
            sellDetail.append(op.getPrice());
            if (op.getOrigQty().compareTo(BigDecimal.ONE) != 0) {
                sellDetail.append("(" + op.getOrigQty() + ")");
            }
            sellDetail.append("&nbsp;&nbsp;");
        }
        klineVo.setSellDetail(sellDetail.toString());


        klineVo.setBuyCount(stock.buyQuantity() + "");
        klineVo.setSellCount(stock.sellQuantity() + "");
        klineVo.setHold(stock.getHolds() + "");

        List<List> datas = new ArrayList<>();
        int max = 0;
        int min = Integer.MAX_VALUE;

        //根据最后80%设置最大和最小值
        List<Kline> lines = klineService.getLines(type);
        double count = lines.size() * (zoomStart / 100);
        int i = 0;
        for (Kline line : lines) {
            //开盘(open)，收盘(close)，最低(lowest)，最高(highest)
            List temp = new ArrayList();
            temp.add(line.getTime().toString(DateTime.Format.HOUR_TO_MINUTE));
            temp.add(line.getOpen());
            temp.add(line.getClose());
            temp.add(line.getLow());
            temp.add(line.getHigh());
            datas.add(temp);

            if (i++ < count) {
                continue;
            }
            if (line.getHigh().intValue() > max) {
                max = line.getHigh().intValue();
            }
            if (line.getLow().intValue() < min) {
                min = line.getLow().intValue();
            }
        }
        if (stock.sellPrice() != null && stock.sellPrice().getPrice().intValue() > max) {
            max = stock.sellPrice().getPrice().intValue();
        }

        if (stock.buyPrice() != null && stock.buyPrice().getPrice().intValue() < min) {
            min = stock.buyPrice().getPrice().intValue();
        }

        klineVo.setData(datas);
        klineVo.setMin(min + "");
        klineVo.setMax(max + "");

        return json(klineVo);
    }
}
