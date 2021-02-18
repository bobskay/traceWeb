package wang.wangby.trace.utils;

import lombok.extern.slf4j.Slf4j;
import wang.wangby.exchange.enums.OrderSide;
import wang.wangby.utils.DateTime;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
public class OrderId {
    public static OrderSide getSide(String clientOrderId) {
        return OrderSide.getById(clientOrderId);
    }

    public static BigDecimal getPrice(String clientOrderId) {
        String[] str = clientOrderId.split("-");
        if (str.length == 2) {
            return new BigDecimal(str[1]);
        }
        return null;
    }

    public static Date getOrderTime(String clientOrderId) {
        try {
            String[] str = clientOrderId.split("-");
            if (str.length == 2) {
                String time = str[0].substring(1);
                DateTime dateTime = new DateTime(time, DateTime.Format.YEAR_TO_SECOND_STRING);
                return dateTime.toDate();
            }
            return null;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }
}
