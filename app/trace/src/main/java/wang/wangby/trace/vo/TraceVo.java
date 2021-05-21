package wang.wangby.trace.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TraceVo {
    private String hold;
    private BigDecimal price;
    private String wallet;
    private BigDecimal base;
    private BigDecimal profit;
    private String buy;
    private String sell;
}

