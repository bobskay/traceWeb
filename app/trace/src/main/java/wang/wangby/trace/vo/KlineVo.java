package wang.wangby.trace.vo;

import lombok.Data;

import java.util.List;

@Data
public class KlineVo {
    private String current;
    private String buy;
    private String sell;

    private String buyCount;
    private String sellCount;
    private String hold;

    //K线价格显示区间
    private String max;
    private String min;

    private String wallet;
    private List<List> data;

    //暂停交易价格
    private String stopPrice;
    //当前价位可以买数量
    private String totalRemain;
    private String currentRemain;
    //取消买入价格
    private String cancelPrice;

    private String sellDetail;


}
