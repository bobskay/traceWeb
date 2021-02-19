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
        if (str.length > 1) {
            return new BigDecimal(str[1]);
        }
        return null;
    }

    public static Date getOrderTime(String clientOrderId) {
        try {
            String[] str = clientOrderId.split("-");
            if (str.length > 1) {
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


    public static String newId(OrderSide side, int price) {
        String pix=side.code;
        return pix + DateTime.current().toString(DateTime.Format.YEAR_TO_SECOND_STRING) + "-" + price;
    }

    public static String cancelId(String cancelId) {
        String[] str=cancelId.split("-");
        if(str.length==2){
            return cancelId+"-"+1;
        }
        int count=Integer.parseInt(str[2]);
        return str[0]+"-"+str[1]+"-"+(count+1);
    }
}
