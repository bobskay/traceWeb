package wang.wangby.trace.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.enums.OrderSide;
import wang.wangby.exchange.response.Order;
import wang.wangby.exchange.socket.listener.AggTradeListener;
import wang.wangby.trace.config.MarketConfig;
import wang.wangby.trace.dto.StockOrderDto;
import wang.wangby.trace.config.Market;
import wang.wangby.trace.config.Rule;
import wang.wangby.trace.model.Stock;
import wang.wangby.trace.model.StockOrder;
import wang.wangby.trace.vo.CurrentOrder;
import wang.wangby.utils.DateTime;
import wang.wangby.utils.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MarketService {


    @Autowired
    Exchange exchange;
    @Autowired
    Rule rule;

    @Autowired
    AggTradeListener aggTradeListener;


    public BigDecimal getPrice() {
        return aggTradeListener.getPrice();
    }
}
