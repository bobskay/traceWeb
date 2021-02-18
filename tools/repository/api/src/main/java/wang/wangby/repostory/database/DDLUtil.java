package wang.wangby.repostory.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.annotation.persistence.Length;
import wang.wangby.annotation.persistence.Table;
import wang.wangby.base.entity.Entity;
import wang.wangby.base.entity.EntityField;
import wang.wangby.base.entity.EntityInfo;
import wang.wangby.repostory.database.dto.ColumnInfo;
import wang.wangby.repostory.database.dto.TableInfo;
import wang.wangby.template.TemplateUtil;
import wang.wangby.utils.Dictionary;
import wang.wangby.utils.*;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class DDLUtil {
    @Autowired
    private TemplateUtil templateUtil;

    //获得建表语句
    public  String getCreate(Class<? extends Entity> clazz){
        TableInfo info=toTableInfo(clazz);
        Map map=new HashMap<>();
        map.put("tableInfo",info);
        String text= FileUtil.getText(this.getClass(),"/codetemplate/create.sql.vm");
        return templateUtil.parseText(text,map);
    }

    public TableInfo toTableInfo(Class<? extends Entity> clazz){
        TableInfo info=new TableInfo();
        Table t= ClassUtil.getAnnotation(clazz, Table.class);
        List columns=new ArrayList<>();
        if(t==null){
            info.setTableName(clazz.getSimpleName());
        }else{
            info.setTableName(t.value());
        }
        EntityInfo etInfo=EntityInfo.getInstance(clazz);
        for(EntityField ef:etInfo.getColumns()){
            Field f=ef.getField();
            Remark remark= ClassUtil.getAnnotation(f, Remark.class);
            if(remark==null){
                continue;
            }
            ColumnInfo col=new ColumnInfo();
            col.setColumnComment(remark.value());
            col.setDataType(toMySqlType(f));
            col.setColumnName(f.getName());
            Length length= ClassUtil.getAnnotation(f, Length.class);
            if(length!=null){
                col.setMaxLength(length.value());
            }
            Id id= ClassUtil.getAnnotation(f, Id.class);
            if(id!=null){
                col.setIsPk(true);
            }
            columns.add(col);
        }
        info.setColumns(columns);
        return info;
    }

    private String toMySqlType(Field f) {
        if(f.getType()==String.class){
            return "varchar";
        }
        if(f.getType()== Date.class){
            return "datetime";
        }
        if(f.getType()==Long.class){
            return "bigint";
        }
        if(f.getType()==Boolean.class){
            return "tinyint";
        }
        if(f.getType()==Integer.class){
            return "int";
        }
        if(ClassUtil.isInstance(f.getType(), Dictionary.class)){
            return "tinyint";
        }
        throw new RuntimeException("无法获取对应的数据库类型:"+f.getType());
    }



    //获得插入的sql
    public String getInsert(List<? extends Entity> list) {

        StrBuilder sb=new StrBuilder();
        for(Entity o:list){
            sb.appendLine(getInsert(o).replaceAll("\\s+"," ")+";");
        }
        return sb.toString();
    }

    public String getInsert(Entity baseModel){
        TableInfo info=toTableInfo(baseModel.getClass());
        Map map=new HashMap<>();
        List<String> values=new ArrayList<>();
        for(ColumnInfo columnInfo:info.getColumnsWithoutPk()){
           Object value= BeanUtil.get(baseModel,columnInfo.getColumnName());
            if(value instanceof String){
                value="'"+value+"'";
            }
            if(value instanceof Date){
                value= "'"+new DateTime(((Date) value).getTime(), DateTime.Format.YEAR_TO_SECOND)+"'";
            }
            values.add(value+",");
        }
        values.add(baseModel.id()+"") ;
        map.put("values",values);
        map.put("tableInfo",info);
        String text= FileUtil.getText(this.getClass(),"/codetemplate/insert.sql.vm");
        return templateUtil.parseText(text,map);
    }

}
