package wang.wangby.base.entity.convertor;

public interface EntityTableConvertor {

    //通过类信息获得表名
    String getTableName(Class clazz);
}
