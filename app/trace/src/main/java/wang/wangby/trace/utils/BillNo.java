package wang.wangby.trace.utils;


import wang.wangby.exchange.enums.OrderSide;
import wang.wangby.utils.DateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BillNo {
    public static String newBuy() {
        String time = DateTime.current().toString(DateTime.Format.YEAR_TO_SECOND_STRING);
        return "B_" + time;
    }

    public static String newSell() {
        String time = DateTime.current().toString(DateTime.Format.YEAR_TO_SECOND_STRING);
        return "S_" + time;
    }

    public static OrderSide getOrderSide(String billNo){
        return OrderSide.getById(billNo);
    }

    /**
     * @param billNo
     * @param price  买入价
     * @return
     */
    public static String close(String billNo, BigDecimal price) {
        price = price.setScale(0, RoundingMode.HALF_UP);
        return billNo + "_" + price + "_C0";
    }

    /**
     * * @param clientOrderId OB_YYYYMMDDHHmmSS_PRICE_C0
     */
    public static BigDecimal getPrice(String clientOrderId) {
        String[] str = clientOrderId.split("_");
        return new BigDecimal(str[2]);
    }

    public static boolean isClose(String clientOrderId) {
        String[] str = clientOrderId.split("_");
        if (str.length != 4) {
            return false;
        }
        return str[3].startsWith("C");
    }

    public static boolean isBuy(String billNo){
        return billNo.startsWith("B");
    }

    public static String increase(String billNo) {
        int index=billNo.lastIndexOf("_C");
        String num=billNo.substring(index+2);
        num=(Integer.parseInt(num)+1)+"";
        return billNo.substring(0,index+2)+num;
    }

}