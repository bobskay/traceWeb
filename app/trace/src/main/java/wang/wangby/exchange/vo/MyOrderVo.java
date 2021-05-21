package wang.wangby.exchange.vo;

import wang.wangby.exchange.enums.OrderSide;
import wang.wangby.trace.config.OrderConfig;
import wang.wangby.trace.model.MyOrder;
import wang.wangby.trace.model.UpInfo;
import wang.wangby.trace.utils.BillNo;
import wang.wangby.utils.DateTime;

import java.math.BigDecimal;

public class MyOrderVo {
    private MyOrder myOrder;
    private OrderConfig orderConfig;
    private BigDecimal currentPrice;
    public MyOrderVo(MyOrder myOrder,OrderConfig orderConfig,BigDecimal currentPrice){
        this.myOrder=myOrder;
        this.orderConfig=orderConfig;
        this.currentPrice =currentPrice;
    }



    public String getBillNo(){
        return myOrder.getBillNo();
    }

    public String getCreatedAt(){
        return new DateTime(myOrder.getCreatedAt()).toString(DateTime.Format.YEAR_TO_SECOND);
    }

    public OrderSide getSide(){
        return BillNo.getOrderSide(myOrder.getBillNo());
    }

    public BigDecimal getQuantity(){
        return myOrder.getQuantity();
    }

    public BigDecimal getBuyPrice(){
        return myOrder.getBuyPrice();
    }

    public BigDecimal getSellPrice(){
        return myOrder.getSellPrice();
    }

    public BigDecimal getProfit(){
        return myOrder.getProfit();
    }

    public String getUpPrice() {
        UpInfo up = myOrder.getUpInfo(currentPrice, orderConfig);
        return up.getUpgradePrice()+"("+up.getDiff()+")";
    }
}
