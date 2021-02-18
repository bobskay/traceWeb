package wang.wangby.base.entity;


import wang.wangby.tools.context.Context;

import java.util.Map;

public interface Entity {


    default Map<String, Object> getExt() {
        return Context.ENTITY_CONTEXT.getExt(this);
    }

    default Long id() {
        return (Long) EntityInfo.getInstance(this.getClass()).getPk().get(this);
    }

    default void id(Long id) {
        EntityInfo.getInstance(this.getClass()).getPk().set(this, id);
    }

    default EntityField field(String fieldName){
        return  EntityInfo.getInstance(this.getClass()).getField(fieldName);
    }

    default <T> T get(String fieldName) {
        EntityField f=field(fieldName);
        if(f!=null&&f.getField()!=null){
            return (T) f.get(this);
        }
        return (T) getExt().get(fieldName);
    }

    /**
     * ext 是否是拓展字段
     */
    default void set(String fieldName, Object value) {
        boolean set = EntityInfo.getInstance(this.getClass()).set(this, fieldName, value);
        if (!set) {
            this.getExt().put(fieldName, value);
        }
    }
}
