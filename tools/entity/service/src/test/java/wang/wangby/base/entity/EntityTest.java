package wang.wangby.base.entity;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EntityTest {

    @Test
    public void get() {
        EntityInfo inf=EntityInfo.getInstance(BookStore.class);
        List<String> all=new ArrayList<>();
        List<String> columns=new ArrayList<>();
        for(EntityField f:inf.getAllFields()){
            all.add(f.getName());
        }
        for(EntityField f:inf.getColumns()){
            columns.add(f.getName());
        }

        log.debug("全部的字段:"+all);
        log.debug("数据库字段:"+columns);

    }
}