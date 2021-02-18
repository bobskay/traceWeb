package wang.wangby.codebuilder.model;

import lombok.Data;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.base.entity.Entity;
import wang.wangby.repostory.database.SqlUtil;
import wang.wangby.repostory.database.dto.TableInfo;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Data
public class DemoModel   implements Entity, Map {

    @Id
    private Long id;

    private String sql;

    private TableInfo tableInfo;

    public Long id() {
        return (Long) this.getExt().get(tableInfo().pkName());
    }

    public void id(Long id) {
        this.getExt().put(tableInfo().pkName(), id);
    }

    public TableInfo tableInfo() {
        if (tableInfo == null) {
            tableInfo = SqlUtil.toTable(sql);
        }
        return tableInfo;
    }

    @Override
    public int size() {
        return this.getExt().size();
    }

    @Override
    public boolean isEmpty() {
        return this.getExt().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.getExt().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.getExt().containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return this.getExt().get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        return this.getExt().put(key+"", value);
    }

    @Override
    public Object remove(Object key) {
        return this.getExt().remove(key);
    }

    @Override
    public void putAll(Map m) {
        this.getExt().putAll(m);
    }

    @Override
    public void clear() {
        this.getExt().clear();
    }

    @Override
    public Set keySet() {
        return this.getExt().keySet();
    }

    @Override
    public Collection values() {
        return this.getExt().values();
    }

    @Override
    public Set<Entry> entrySet() {
        Set set=this.getExt().entrySet();
        return set;
    }
}
