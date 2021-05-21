package wang.wangby.exchange.dto;

import lombok.extern.slf4j.Slf4j;
import wang.wangby.exchange.enums.OrderSide;
import wang.wangby.exchange.response.ApiResponse;
import lombok.Data;

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
    private BigDecimal stopPrice;
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
        }
       return null;
    }

}
