package wang.wangby.exchange.dto;

import wang.wangby.exchange.response.ApiResponse;
import lombok.Data;
import wang.wangby.utils.DateTime;

import java.math.BigDecimal;

@Data
public class Kline extends ApiResponse {
    private DateTime time;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
}
