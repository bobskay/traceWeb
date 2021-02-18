package wang.wangby.template
        ;

import wang.wangby.utils.DateTime;
import wang.wangby.utils.StringUtil;

import java.util.Date;

public class SimpleObject {
    private static SimpleObjectConvertor CONVERTOR = new SimpleObjectConvertor() {
        @Override
        public String toJson(Object target) {
            return "SimpleObjectConvertor未初始化";
        }

        @Override
        public String htmlEscape(String str) {
            return str;
        }
    };

    // 在调用toJson和reamrk之前,需要先初始化CONVERTOR
    public static void init(SimpleObjectConvertor convertor) {
        if (convertor != null) {
            CONVERTOR = convertor;
        }
    }

    private Object target;

    public SimpleObject(Object value) {
        this.target = value;
    }

    public String getDate() {
        if (target instanceof Date) {
            String s = new DateTime((Date) target, DateTime.Format.YEAR_TO_DAY).toString();
            return s;
        }
        if (target instanceof Long) {
            Long time = (Long) target;
            if (time == 0) {
                return "";
            }
            return new DateTime(time).toString(DateTime.Format.YEAR_TO_DAY);
        }
        return toString();
    }

    public String getJson() {
        return CONVERTOR.toJson(target);
    }

    public String showNs() {
        if (target instanceof Long) {
            return DateTime.showNs(((Long) target).longValue());
        }
        return "";
    }

    //返回包括毫秒的日期
    public String toMsTime() {
        if (target instanceof Long) {
            String s = new DateTime((Long) target, DateTime.Format.YEAR_TO_MILLISECOND).toString();
            return s;
        }
        return "";
    }

    public String getDateTime() {
        if (target instanceof Date) {
            String s = new DateTime((Date) target, DateTime.Format.YEAR_TO_SECOND).toString();
            return s;
        }
        if (target instanceof Long) {
            Long time = (Long) target;
            if (time == 0) {
                return "";
            }
            return new DateTime(time).toString(DateTime.Format.YEAR_TO_SECOND);
        }
        return toString();
    }

    // 用html格式化
    public String getHtml() {
        if (target == null) {
            return "";
        }
        return CONVERTOR.htmlEscape(target + "");
    }

    public String getYesOrNo() {
        if (StringUtil.isEmpty(target)) {
            return "";
        }
        if (target instanceof Boolean) {
            if ((Boolean) target) {
                return "是";
            }
            return "否";
        }
        if ((target + "").equals("0")) {
            return "否";
        }
        return "是";
    }

    // 是否是原生对象
    public static boolean isPrimitiveType(Object value) {
        if (value instanceof Date) {
            return true;
        }
        String className = value.getClass().getName();
        return className.startsWith("java.lang") || className.indexOf('.') == -1;
    }
}
