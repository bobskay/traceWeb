package wang.wangby.trace.utils;

import wang.wangby.exchange.dto.OpenOrder;
import wang.wangby.trace.model.Stock;

import java.math.BigDecimal;
import java.util.*;

public class ShowUtil {

    public static String sellDetail(Stock stock) {
        StringBuilder sellDetail = new StringBuilder();
        Map<Integer,List<OpenOrder>> areaMap=new TreeMap<>();
        List<OpenOrder> sells=new ArrayList<>(stock.sells());
        Collections.sort(sells,(Comparator.comparing(OpenOrder::getPrice)));
        for (OpenOrder op : sells) {
            Integer price=op.getPrice().intValue();
            price=price/10*10;
            List<OpenOrder> ods= areaMap.get(price);
            if(ods==null){
                ods=new ArrayList<>();
            }
            ods.add(op);
            areaMap.put(price,ods);
        }

        areaMap.forEach(((price, openOrders) -> {
            StringBuilder temp=new StringBuilder();
            for(OpenOrder op:openOrders){
                int buyPrice=OrderId.getPrice(op.getClientOrderId()).intValue();
                int diff=(op.getPrice().intValue()-buyPrice);
                temp.append(buyPrice+"+"+diff);
                if (op.getOrigQty().compareTo(BigDecimal.ONE) != 0) {
                    temp.append("(" + op.getOrigQty() + ")");
                }
                temp.append("&nbsp;&nbsp;&nbsp;");
            }
            sellDetail.append(price+": "+temp);
            sellDetail.append("<br>");
        }));

        return sellDetail.toString();
    }

    public static String bayDetail(Stock stock,BigDecimal current) {
        List<OpenOrder> sells=new ArrayList<>(stock.sells());
        Collections.sort(sells,((o1, o2) -> {
            return o2.getPrice().compareTo(o1.getPrice());
        }));

        StringBuilder sellDetail = new StringBuilder();
        for(OpenOrder op:sells){
            int buyPrice=OrderId.getPrice(op.getClientOrderId()).intValue();
            int diff=(op.getPrice().intValue()-buyPrice);
            if(Math.abs(buyPrice-current.intValue())<10){
                sellDetail.append(buyPrice+"+"+diff);
                if (op.getOrigQty().compareTo(BigDecimal.ONE) != 0) {
                    sellDetail.append("(" + op.getOrigQty() + ")");
                }
                sellDetail.append("&nbsp;&nbsp;&nbsp;");
            }

        }
        return sellDetail.toString();
    }
}
