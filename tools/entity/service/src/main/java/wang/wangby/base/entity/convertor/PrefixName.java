package wang.wangby.base.entity.convertor;

public class PrefixName implements EntityTableConvertor {
    private String prefix;
    public PrefixName(String prefix){
        this.prefix=prefix;
    }
    @Override
    public String getTableName(Class clazz) {
        return prefix+clazz.getSimpleName().toLowerCase();
    }
}
