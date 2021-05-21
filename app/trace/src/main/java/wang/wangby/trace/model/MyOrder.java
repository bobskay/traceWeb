package wang.wangby.trace.model;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.base.entity.Entity;
import wang.wangby.trace.config.OrderConfig;
import wang.wangby.trace.utils.BillNo;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MyOrder implements Entity {


    @Id
    @Remark("主键")
    private Long id;
    @Remark("创建时间")
    private Date createdAt;
    @Remark("订单号")
    private String billNo;
    @Remark("数量")
    private BigDecimal quantity;
    @Remark("成交价格")
    private BigDecimal buyPrice;
    @Remark("卖出价格")
    private BigDecimal sellPrice;

    public boolean isBuy() {
        return BillNo.isBuy(billNo);
    }

    public boolean isActive() {
        return sellPrice.compareTo(buyPrice) > 0;
    }

    public BigDecimal getProfit(){
        return this.sellPrice.subtract(this.buyPrice).multiply(this.quantity);
    }

    private BigDecimal reOrderPrice(BigDecimal addPrice) {
        if (isBuy()) {
            if (isActive()) {
                BigDecimal diff=sellPrice.add(addPrice).subtract(buyPrice).multiply(new BigDecimal(2));
                return buyPrice.add(diff);
            }
            return buyPrice.add(addPrice);
        } else {
            if (isActive()) {
                BigDecimal dff=sellPrice.add(addPrice).subtract(buyPrice).multiply(new BigDecimal(2));
                return sellPrice.subtract(dff);
            }
            return sellPrice.subtract(addPrice);
        }
    }

    public UpInfo getUpInfo(BigDecimal price,OrderConfig orderConfig ) {
        BigDecimal openPrice=orderConfig.getStep();
        BigDecimal reOrderPrice = reOrderPrice(openPrice);
        UpInfo upInfo = new UpInfo();
        upInfo.setUpgradePrice(reOrderPrice);

        if (isBuy()) {
            BigDecimal profit=reOrderPrice.subtract(this.buyPrice).divide(new BigDecimal(2));
            upInfo.setReOrderPrice(buyPrice.add(profit));

            if (price.compareTo(reOrderPrice) >= 0) {
                upInfo.setUp(true);
                upInfo.setDiff(BigDecimal.ZERO);
            } else {
                upInfo.setUp(false);
                upInfo.setDiff(reOrderPrice.subtract(price));
            }
        } else {
            BigDecimal profit=this.sellPrice.subtract(reOrderPrice).divide(new BigDecimal(2));
            upInfo.setReOrderPrice(sellPrice.subtract(profit));
            if (reOrderPrice.compareTo(price) >= 0) {
                upInfo.setUp(true);
                upInfo.setDiff(BigDecimal.ZERO);
            } else {
                upInfo.setUp(false);
                upInfo.setDiff(reOrderPrice.subtract(price));
            }
        }

        return upInfo;
    }


}


