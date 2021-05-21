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
                return sellPrice.add(addPrice);
            }
            return buyPrice.add(addPrice);
        } else {
            if (isActive()) {
                return buyPrice.subtract(addPrice);
            }
            return sellPrice.subtract(addPrice);
        }
    }

    public UpInfo getUpInfo(BigDecimal price,OrderConfig orderConfig ) {

        BigDecimal openPrice=orderConfig.getUpgradePrice();
        BigDecimal step=orderConfig.getStep();
        BigDecimal minStop=orderConfig.getMinStop();

        BigDecimal reOrderPrice = reOrderPrice(openPrice);
        UpInfo upInfo = new UpInfo();
        upInfo.setUpgradePrice(reOrderPrice);
        BigDecimal reorder=reOrderPrice(step);


        if (isBuy()) {
            if(price.subtract(reorder).compareTo(minStop)<0){
                reorder=price.subtract(minStop);
            }

            if (price.compareTo(reOrderPrice) >= 0) {
                upInfo.setUp(true);
                upInfo.setDiff(BigDecimal.ZERO);
            } else {
                upInfo.setUp(false);
                upInfo.setDiff(reOrderPrice.subtract(price));
            }
        } else {
            if(reorder.subtract(price).compareTo(minStop)<0){
                reorder=price.add(minStop);
            }

            if (reOrderPrice.compareTo(price) >= 0) {
                upInfo.setUp(true);
                upInfo.setDiff(BigDecimal.ZERO);
            } else {
                upInfo.setUp(false);
                upInfo.setDiff(reOrderPrice.subtract(price));
            }
        }
        upInfo.setReOrderPrice(reorder);
        return upInfo;
    }


}


