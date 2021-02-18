package wang.wangby.base.entity;


import lombok.Data;
import lombok.SneakyThrows;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.annotation.persistence.OneToMany;
import wang.wangby.annotation.persistence.OneToOne;
import wang.wangby.utils.ClassUtil;
import wang.wangby.utils.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class EntityField {
    private String name;
    private Class type;
    private Class realClass;
    private OneToOne oneToOne;
    private OneToMany oneToMany;
    private EntityInfo entityInfo;
    private Id id;
    private String fullName;
    private Field field;

    private EntityField(){}

    public static EntityField oneToMany(EntityInfo entityInfo,OneToMany oneToMany){
        EntityField f=new EntityField();
        Class target=oneToMany.target();
        String name=oneToMany.fieldName();
        if(StringUtil.isEmpty(name)){
            name=StringUtil.firstLower(target.getSimpleName())+"List";
        }
        f.entityInfo=entityInfo;
        f.name=name;
        f.type=List.class;
        f.realClass=target;
        f.oneToMany=oneToMany;
        f.fullName=entityInfo.getEntityClass().getName()+"."+name;
        return f;
    }

    public static EntityField oneToOne(EntityInfo entityInfo,OneToOne oneToOne){
        EntityField f=new EntityField();
        Class target=oneToOne.target();
        String name=oneToOne.fieldName();
        if(StringUtil.isEmpty(name)){
            name=StringUtil.firstLower(target.getSimpleName());
        }
        f.name=name;
        f.type=target;
        f.realClass=target;
        f.oneToOne=oneToOne;
        f.fullName=entityInfo.getEntityClass().getName()+"."+name;
        f.entityInfo=entityInfo;
        return f;
    }

    public EntityField(EntityInfo entityInfo, Field field) {
        this.name=field.getName();
        this.type=field.getType();
        if(isList()){
            this.realClass= ClassUtil.getFirstGenericType(field.getGenericType());
        }else{
            this.realClass=type;
        }
        this.oneToOne= ClassUtil.getAnnotation(field, OneToOne.class);
        this.oneToMany= ClassUtil.getAnnotation(field, OneToMany.class);
        this.id= ClassUtil.getAnnotation(field, Id.class);
        this.entityInfo =entityInfo;
        this.fullName= this.entityInfo.getEntityClass().getName()+"."+name;
        this.field=field;
        this.field.setAccessible(true);
    }

    public boolean isList(){
        return type.isAssignableFrom(List.class);
    }

    //关联的外部表的对应字段名称
    public String getReferenceName(){
        if(oneToOne!=null){
            return referenceName(oneToOne.references());
        }
        if(oneToMany!=null){
            return referenceName(oneToMany.references());
        }
        throw new RuntimeException("当前字段未配置关联表"+this.fullName);
    }

    public String getFkName(){
        if(oneToOne!=null){
            return fkName(oneToOne.fk(),oneToOne.references());
        }
        if(oneToMany!=null){
            return fkName(oneToMany.fk(),oneToMany.references());
        }
        throw new RuntimeException("当前字段未配置关联表"+this.fullName);
    }

    private String fkName(String fk, String reference) {
        if(StringUtil.isNotEmpty(fk)){
            return fk;
        }
        if(StringUtil.isNotEmpty(reference)){
            return reference;
        }
        if(oneToOne!=null){
            return EntityInfo.getInstance(realClass).getPk().getName();
        }else{
            return entityInfo.getPk().getName();
        }

    }

    private String referenceName(String reference) {
        if(StringUtil.isNotEmpty(reference)){
            return reference;
        }
        if(oneToOne!=null){
            return EntityInfo.getInstance(realClass).getPk().getName();
        }else{
            return entityInfo.getPk().getName();
        }
    }

    public EntityField getFkField() {
        EntityField field= entityInfo.getField(this.getFkName());
        if(field==null){
            throw new RuntimeException("找不到字段"+this.entityInfo.getEntityClass().getName()+"."+this.getFkName());
        }
        return field;
    }

    public boolean isPk() {
        return id!=null;
    }

    @SneakyThrows
    public Object get(Entity entity) {
        return field.get(entity);
    }

    public List getList(List<? extends Entity> entity) {
        List list=new ArrayList();
        if(entity.size()==0){
            return list;
        }
        entity.forEach(b->{
            list.add(get(b));
        });
        return list;
    }

    @SneakyThrows
    public boolean set(Entity entity,Object value) {
        if(field==null){
            throw new RuntimeException("请调用entity的set方法为此字段赋值:"+this.fullName);
        }
        field.set(entity,value);
        return true;
    }

    public EntityField getReferenceField() {
        return EntityInfo.getInstance(realClass).getField(this.getReferenceName());
    }


    public boolean isColumn() {
        return field.getDeclaringClass().getSuperclass()==Object.class;
    }
}

