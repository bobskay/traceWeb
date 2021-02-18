package wang.wangby.exchange.dto;

import wang.wangby.exchange.response.ApiResponse;
import lombok.Data;

@Data
public class CancelAll extends ApiResponse {
    /**
     * code : 200
     * msg : The operation of cancel all open order is done.
     */
    private Integer code;
    private String msg;
}

