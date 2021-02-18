package wang.wangby.exchange.dto;

import wang.wangby.exchange.response.ApiResponse;
import lombok.Data;

@Data
public class UserDataStream extends ApiResponse {
    private String listenKey;
}
