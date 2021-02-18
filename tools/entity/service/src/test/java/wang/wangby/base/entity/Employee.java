package wang.wangby.base.entity;

import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;

@Remark("雇员")
public class Employee {
    @Id
    @Remark("主键")
    private Long employeeId;

    @Id
    @Remark("店铺id")
    private Long bookStoreId;
}
