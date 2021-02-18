package wang.wangby.repostory.selector;

import wang.wangby.base.entity.Entity;
import wang.wangby.utils.StringUtil;

import java.util.List;

public class Condition {

    private String field;
    private Entity entity;
    private Selector selector;

    public Condition(Selector selector, Entity entity, String field) {
        this.field = field;
        this.entity = entity;
        this.selector = selector;
    }

    public Selector in(List list) {
        String sql = QueryUtil.in(field, list);
        String old = (String) entity.getExt().get("condition");
        if (!StringUtil.isEmpty(old)) {
            sql = old + " and (" + sql + ")";
        }
        entity.getExt().put("condition", sql);
        return selector;
    }

    public Selector eq(Object value) {
        entity.set(field,value);
        return selector;
    }
}
