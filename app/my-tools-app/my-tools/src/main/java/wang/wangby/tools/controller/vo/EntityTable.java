package wang.wangby.tools.controller.vo;

import lombok.Data;
import wang.wangby.base.entity.EntityInfo;
import wang.wangby.repostory.database.dto.TableInfo;

@Data
public class EntityTable {
    private TableInfo tableInfo;
    private EntityInfo entityInfo;
    private String createSql;
}
