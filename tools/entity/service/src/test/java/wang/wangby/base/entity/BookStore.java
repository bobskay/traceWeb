package wang.wangby.base.entity;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.annotation.persistence.OneToMany;

@Data
@OneToMany(target = Book.class,fk="bookStoreId",references = "storeId")
@OneToMany(target = Employee.class)
public class BookStore implements Entity{
    @Id
    @Remark("主键")
    private Long bookStoreId;
}
