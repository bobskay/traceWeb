package wang.wangby.base.entity;

import lombok.Getter;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.OneToMany;
import wang.wangby.annotation.persistence.OneToManys;
import wang.wangby.annotation.persistence.OneToOne;
import wang.wangby.annotation.persistence.OneToOnes;
import wang.wangby.utils.CacheUtil;
import wang.wangby.utils.ClassUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityInfo {
    private static Map<Class, EntityInfo> map=new ConcurrentHashMap<>();
    @Getter
    private List<EntityField> allFields;
    @Getter
    private List<EntityField> columns;
    @Getter
    private EntityField pk;
    @Getter
    private Class entityClass;

    public static EntityInfo getInstance(Class<? extends Entity> entityClass){
        return CacheUtil.get(map,entityClass,()->new EntityInfo(entityClass),map);
    }

    private EntityInfo(Class<? extends Entity> entityClass) {
        this.entityClass=entityClass;
        allFields=new ArrayList<>();
        columns =new ArrayList<>();

        List<Field> fs=ClassUtil.getFieldsByAnnotation(entityClass,Remark.class);
        fs.addAll(ClassUtil.getFieldsByAnnotation(entityClass,OneToOne.class));
        fs.addAll(ClassUtil.getFieldsByAnnotation(entityClass,OneToMany.class));

        for(Field field:fs){
            EntityField f=new EntityField(this,field);
            columns.add(f);
            allFields.add(f);
            if(f.isPk()){
                pk=f;
            }
        }
        if(pk==null){
            throw  new RuntimeException(entityClass.getName()+"未设置主键");
        }

        OneToManys manys=ClassUtil.getAnnotation(entityClass, OneToManys.class);
        if(manys!=null){
            for(OneToMany many:manys.value() ){
                EntityField field=EntityField.oneToMany(this,many);
                allFields.add(field);
            }
        }else{
            OneToMany many=ClassUtil.getAnnotation(entityClass, OneToMany.class);
            if(many!=null){
                EntityField field=EntityField.oneToMany(this,many);
                allFields.add(field);
            }
        }


        OneToOnes ones=ClassUtil.getAnnotation(entityClass, OneToOnes.class);
        if(ones!=null){
            for(OneToOne one:ones.value() ){
                EntityField field=EntityField.oneToOne(this,one);
                allFields.add(field);
            }
        }else{
            OneToOne one=ClassUtil.getAnnotation(entityClass, OneToOne.class);
            if(one!=null){
                EntityField field=EntityField.oneToOne(this,one);
                allFields.add(field);
            }
        }
    }

    public EntityField getField(String fieldName){
        for(EntityField f:allFields){
            if(f.getName().equals(fieldName)){
                return f;
            }
        }
        return null;
    }

    //设置字段值,如果字段不存在返回false
    public boolean set(Entity obj,String fieldName, Object value) {
        for(EntityField field:allFields){
            if(field.getField()!=null&&field.getName().equals(fieldName)){
                return field.set(obj,value);
            }
        }
        return false;
    }

    public List<EntityField> getColumns() {
        return columns;
    }
}