package wang.wangby.trace.config;

import lombok.Data;
import org.springframework.stereotype.Component;
import wang.wangby.annotation.Remark;

import java.math.BigDecimal;

@Data
@Component
public class RunningInfo {

    @Remark("基准价格")
    private BigDecimal basePrice;


}
