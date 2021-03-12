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
    private List<Kline> hourLine;
    private List<Kline> fourHoarLine;
    private List<Kline> dayLine;
    private List<Kline> fiveLine;
    private List<Kline> fifteenLine;

    public List<Kline> getLines(String code) {
        CandlestickInterval interval = CandlestickInterval.getByCode(code);
        switch (interval) {
            case ONE_MINUTE:
                return minuteLine;
            case FIVE_MINUTES:
                return fiveLine;
            case FIFTEEN_MINUTES:
                return fifteenLine;
            case HALF_HOURLY:
                return halfHoarLine;
            case HOURLY:
                return hourLine;
            case FOUR_HOURLY:
                return fourHoarLine;
            case DAILY:
                return dayLine;
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
        if (fiveLine != null) {
            lasts.add(fiveLine.get(fiveLine.size() - 1));
        }
        if (fifteenLine != null) {
            lasts.add(fifteenLine.get(fifteenLine.size() - 1));
        }
        if (halfHoarLine != null) {
            lasts.add(halfHoarLine.get(halfHoarLine.size() - 1));
        }
        if (hourLine != null) {
            lasts.add(hourLine.get(hourLine.size() - 1));
        }
        if (fourHoarLine != null) {
            lasts.add(fourHoarLine.get(fourHoarLine.size() - 1));
        }
        if(dayLine!=null){
            lasts.add(dayLine.get(dayLine.size() - 1));
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


    @Scheduled(cron = "1 * * * * ? ")
    public void oneMinute() {
        minuteLine = exchange.kline(CandlestickInterval.ONE_MINUTE);
    }

    @Scheduled(cron = "2 0/5 * * * ? ")
    public void fiveMinute() {
        fiveLine = exchange.kline(CandlestickInterval.FIVE_MINUTES);
        fifteenLine= exchange.kline(CandlestickInterval.FIFTEEN_MINUTES);
    }

    @Scheduled(cron = "3 0/30 * * * ? ")
    public void halfHour() {
        halfHoarLine = exchange.kline(CandlestickInterval.HALF_HOURLY);
    }

    @Scheduled(cron = "4 0 * * * ? ")
    public void hour() {
        hourLine= exchange.kline(CandlestickInterval.HOURLY);
        fourHoarLine = exchange.kline(CandlestickInterval.FOUR_HOURLY);
        dayLine = exchange.kline(CandlestickInterval.DAILY);
    }

    @Override
    public void run(String... args) throws Exception {
        oneMinute();
        fiveMinute();
        halfHour();
        hour();
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

