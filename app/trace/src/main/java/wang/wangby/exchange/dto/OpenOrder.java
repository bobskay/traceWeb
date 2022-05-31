package wang.wangby.exchange.dto;

import lombok.extern.slf4j.Slf4j;
import wang.wangby.exchange.enums.OrderSide;
import wang.wangby.exchange.response.ApiResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import wang.wangby.trace.utils.OrderId;
import wang.wangby.utils.DateTime;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Slf4j
public class OpenOrder extends ApiResponse {


    private Long orderId;
    private String symbol;
    private String status;
    private String clientOrderId;
    private BigDecimal price;
    private String avgPrice;
    private BigDecimal origQty;
    private String executedQty;
    private String cumQuote;
    private String timeInForce;
    private String type;
    private Boolean reduceOnly;
    private Boolean closePosition;
    private String side;
    private String positionSide;
    private String stopPrice;
    private String workingType;
    private Boolean priceProtect;
    private String origType;
    private Long time;
    private Long updateTime;


    public OrderSide getSide(){
        for(OrderSide s:OrderSide.values()){
            if(clientOrderId.startsWith(s.code)){
                return s;
            }
            if(s.toString().equalsIgnoreCase(side)){
                return s;
            }
        }

       return null;
    }

    public String getOrderTime(){
        try{
            Date date= OrderId.getOrderTime(this.clientOrderId);
            if(date==null){
                return "";
            }
            return new DateTime(date).toString(DateTime.Format.YEAR_TO_SECOND);
        }catch (Exception ex){
            return "";
        }



    }

    //下单价格
    public String getOrderPrice(){
        return OrderId.getPrice(this.clientOrderId)+"";
    }


}
