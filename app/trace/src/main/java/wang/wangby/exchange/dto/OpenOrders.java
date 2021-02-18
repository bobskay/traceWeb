package wang.wangby.exchange.dto;

import com.alibaba.fastjson.JSONArray;
import wang.wangby.exchange.response.ApiResponse;

import lombok.Data;
import wang.wangby.exchange.response.ListResponse;
import wang.wangby.serialize.json.JsonUtil;

import java.util.ArrayList;
import java.util.List;

@Data
public class OpenOrders extends ApiResponse implements ListResponse {
    private List<OpenOrder> openOrders;

    @Override
    public void addResult(String response) {
        JSONArray array = JSONArray.parseArray(response);
        openOrders = new ArrayList<>();
        for (Object o : array) {
            OpenOrder openOrder = JsonUtil.INSTANCE.toBean(o.toString(), OpenOrder.class);
            openOrders.add(openOrder);
        }
    }
}
