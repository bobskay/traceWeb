package wang.wangby.trace.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.dto.Kline;
import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.exchange.enums.CandlestickInterval;
import wang.wangby.serialize.json.JsonUtil;
import wang.wangby.trace.model.Stock;
import wang.wangby.utils.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Service
@Slf4j
public class KlineService implements CommandLineRunner {

    @Autowired
    Exchange exchange;
    @Autowired
    MarketService marketService;


    private List<Kline> minuteLine;
    private List<Kline> halfHoarLine;

    public List<Kline> getLines(String code) {
        CandlestickInterval interval = CandlestickInterval.getByCode(code);
        switch (interval) {
            case ONE_MINUTE:
                return minuteLine;
            case HALF_HOURLY:
                return halfHoarLine;
        }
        throw new RuntimeException("未统计" + interval);
    }

    @Scheduled(cron = "0/5 * * * * ? ")
    public void updateLine() {
        BigDecimal price = marketService.getPrice();
        if (price == null) {
            return;
        }

        List<Kline> lasts = new ArrayList<>();
        if (minuteLine != null) {
            lasts.add(minuteLine.get(minuteLine.size() - 1));
        }
        if (halfHoarLine != null) {
            lasts.add(halfHoarLine.get(halfHoarLine.size() - 1));
        }


        for (Kline kline : lasts) {
            kline.setClose(marketService.getPrice());
            if (kline.getHigh().compareTo(price) < 0) {
                kline.setHigh(price);
            }
            if (kline.getLow().compareTo(price) > 0) {
                kline.setLow(price);
            }
        }
    }


    @Scheduled(cron = "0 * * * * ? ")
    public void oneMinute() {
        minuteLine = exchange.kline(CandlestickInterval.ONE_MINUTE);
    }


    @Scheduled(cron = "0 0/30 * * * ? ")
    public void halfHour() {
        halfHoarLine = exchange.kline(CandlestickInterval.HALF_HOURLY);
    }

    @Override
    public void run(String... args) throws Exception {
        oneMinute();
        halfHour();
    }

    public BigDecimal getHigh(CandlestickInterval halfHourly) {
        if (halfHoarLine == null) {
            halfHoarLine = exchange.kline(CandlestickInterval.HALF_HOURLY);
        }
        BigDecimal high = BigDecimal.ZERO;
        for (Kline l : halfHoarLine) {
            if (l.getHigh().compareTo(high) > 0) {
                high = l.getHigh();
            }
        }
        return high;
    }
}

