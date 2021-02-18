package wang.wangby.utils;

//自定义的枚举基类
public interface Dictionary {
    default String getKey() {
        return this.toString();
    }

    default Integer getValue() {
        try {
            return (Integer) this.getClass().getMethod("ordinal").invoke(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default String getText() {
        return this.toString();
    }
}
