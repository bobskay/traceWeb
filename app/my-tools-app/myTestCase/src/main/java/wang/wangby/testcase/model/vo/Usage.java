package wang.wangby.testcase.model.vo;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.utils.StringUtil;

@Data
@Remark("容量有限的统计项")
public class Usage {
    @Remark("已经使用")
    private Long used;
    @Remark("最大可用")
    private Long max;
    @Remark("已提交")
    private Long committed;
    @Remark("初始值")
    private Long init;

    public double getPercent(){
        if(max==null||used==null){
            return 0;
        }
        if(max<=0){
            return 0;
        }
        double p=(used+0D)/(max+0D)*100;
        return StringUtil.round(p);
    }
}
