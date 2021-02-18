package wang.wangby.testcase.controller.vo;

import lombok.Data;
import wang.wangby.annotation.Remark;

import java.util.Map;

@Data
public class CpuTestInfo {
    @Remark("是否正在运行")
    private Boolean running;
    @Remark("任务数量")
    private Integer taskCount;
    @Remark("线程池信息")
    private Map<Object, Object> poolInfo;
}
