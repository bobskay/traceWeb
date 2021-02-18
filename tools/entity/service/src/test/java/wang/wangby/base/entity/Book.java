package wang.wangby.base.entity;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.OneToOne;
import wang.wangby.test.entity.BookInfoDto;

@Data
@OneToOne(target=BookStore.class,fk="storeId",references = "bookStoreId")
public class Book extends BookInfoDto implements Entity {
    @Remark("书店id")
    private Long stroeId;
}
