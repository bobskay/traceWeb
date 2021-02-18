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

    private String max;
    private String min;

    private List<List> data;

}
