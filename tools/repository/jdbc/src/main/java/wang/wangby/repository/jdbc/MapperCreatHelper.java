package wang.wangby.repository.jdbc;

import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.utils.ClassUtil;
import wang.wangby.utils.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MapperCreatHelper {

    private Class entityClass;
    private List<Field> fields;
    private String tableName;

    public MapperCreatHelper(Class entityClass,String tableName) {
        this.entityClass = entityClass;
        fields = new ArrayList();
        this.tableName=tableName;
        for (Field f : ClassUtil.getFieldsByAnnotation(entityClass, Remark.class)) {
            //只生成Dto里配了remark的字段
            if(f.getDeclaringClass().getSuperclass()!=Object.class){
                continue;
            }
            fields.add(f);
        }
    }

    public String getEntityClass(){
        return entityClass.getName();
    }

    //获取所有字段名,用逗号隔开
    public String getFieldNames() {
        return StringUtil.join(fields, f -> f.getName());
    }

    //获取所有字段名,用#{}包裹
    public String getValuedNames() {
        return StringUtil.join(fields, f -> "#{" + f.getName() + "}");
    }

    //获取所有字段名,用#{}包裹,并加上前缀
    public String getValuedNames(String prefix) {
        return StringUtil.join(fields, f -> "#{" + prefix + "." + f.getName() + "}");
    }

    public String getTableName() {
        return tableName;
    }

    //获得主键
    public Field getPk() {
        for (Field f : this.fields) {
            if (isPk(f)) {
                return f;
            }
        }
        throw new RuntimeException("找不到主键:" + entityClass.getName());
    }

    private boolean isPk(Field f) {
        Id id = ClassUtil.getAnnotation(f, Id.class);
        return id != null;
    }

    public String getPkName() {
        return getPk().getName();
    }

    public String getPkEqual() {
        Field f = this.getPk();
        return getEqual(f);
    }

    //获取所有字段名,转为name=#{name}的形式
    public String getEqual(Field field) {
        String column = field.getName();
        return column + "=#{" + field.getName() + "}";
    }

    public String getSetEqual() {
        StringBuilder sb = new StringBuilder();
        for (Field f : this.fields) {
            if (isPk(f)) {
                continue;
            }
            sb.append(getEqual(f) + ",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    //获取所有字段用<if test="bane != null"></if>包裹,if里面的值为  name=#{name}
    //如果是update语句就跳过主键并且以逗号结尾,如果是select就以and 开头
    public String getIfNotNull(boolean update) {
        return StringUtil.join(this.fields, f -> {
            if (update) {
                if (isPk(f)) {
                    return "";
                }
            }
            String str = ifNotNull(f);
            str += "\n\t";
            if (update) {
                str += getEqual(f) + ",";
            } else {
                str += "and " + getEqual(f);
            }

            str += "\n";
            str += "</if>";
            return str;
        }, "\n");
    }

    //获得字段是否是null的mybatis语句,字符串要多判断一个是否=''
    private String ifNotNull(Field f) {
        if (f.getType() == String.class) {
            return "<if test=\"" + f.getName() + "!=null and " + f.getName() + "!=''\">";
        }
        return "<if test=\"" + f.getName() + "!=null\">";
    }
}
