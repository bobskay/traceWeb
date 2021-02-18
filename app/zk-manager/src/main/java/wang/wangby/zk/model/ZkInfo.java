package wang.wangby.zk.model;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.base.entity.Entity;

@Data
public class ZkInfo implements Entity {

    @Id
    @Remark("主键")
    private Long id;

    @Remark("地址")
    private String address;

    @Remark("名称")
    private String name;
}
