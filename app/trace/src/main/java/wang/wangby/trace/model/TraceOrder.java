package wang.wangby.trace.model;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.base.entity.Entity;
import wang.wangby.trace.utils.TimeUtil;
import wang.wangby.utils.DateTime;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TraceOrder  implements Entity {
    @Id
    @Remark("主键")
    private Long id;
    @Remark("买入订单id")
    private String buyOrderId;
    @Remark("买入时的价格")
    private BigDecimal start;
    @Remark("买入价")
    private BigDecimal buy;

    @Remark("卖出订单id")
    private String sellOrderId;
    @Remark("卖出价")
    private BigDecimal sell;

    @Remark("买单创建时间")
    private Date createdAt;
    @Remark("买单成交")
    private Date buyFinish;
    @Remark("卖单成交")
    private Date finishAt;

    @Remark("交易数量")
    private BigDecimal quantity;


    public String getBuyUsed(){
        return TimeUtil.showTime(createdAt,buyFinish);
    }

    public String getSellUsed(){
        if(buyFinish==null||finishAt==null){
            return "-";
        }
        return TimeUtil.showTime(buyFinish,finishAt);
    }
    public String getTotalUsed(){
        return TimeUtil.showTime(createdAt,finishAt);
    }



}
