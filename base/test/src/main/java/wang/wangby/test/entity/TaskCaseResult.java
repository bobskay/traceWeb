package wang.wangby.test.entity;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.annotation.persistence.Length;

import java.util.Date;

@Data
public class TaskCaseResult  {
    @Id
    @Remark("主键")
    private Long id;
    @Remark("任务标识")
    @Length(100)
    private String caseId;
    @Remark("统计项")
    @Length(255)
    private String metric;
    @Remark("统计值")
    private Long metricValue;
    @Remark("运行时间")
    private Date runningTime;
}
