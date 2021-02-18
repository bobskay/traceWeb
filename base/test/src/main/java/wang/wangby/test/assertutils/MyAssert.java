package wang.wangby.test.assertutils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import wang.wangby.log.LogUtil;
import wang.wangby.utils.FileUtil;
import wang.wangby.utils.StringUtil;

import java.util.Collection;

@Slf4j
public class MyAssert {

    public static void stringEqual(String result, String expect) {
        stringEqual(result,expect,new Exception("字符串相等验证失败"));
    }

    public static void stringEqual(String result, String expect,Exception caller) {
        log.debug("验证两个字符串是否相等\n" + result + "\n" + expect);
        if (!result.equals(expect)) {
            String message = "字符串相等验证失败";
            LogUtil.debugCaller(caller);
            throw new RuntimeException(message);
        }
    }

    public static void notEmpty(Object o) {
        LogUtil.debugCaller("开始判断对象是否为空");
        if (StringUtil.isEmpty(o)) {
            throw new RuntimeException("对象为空,判断失败:" + o);
        } else if (o instanceof Collection) {
            Collection c = (Collection) o;
            if (c.size() == 0) {
                throw new RuntimeException("集合元素个数为0" + o.getClass());
            }
        }
        log.debug("\n" + toJson(o));
    }

    public static void equalTo(Object result,Object expect){
        if(result instanceof String){
            stringEqual(result+"",expect+"",new Exception("字符串相等验证失败"));
        }
    }

    //将list转为js,每条数据1行
    public static String collectionToJs(Collection collection) {
        StringBuilder sb=new StringBuilder();
        for(Object o:collection){
            String js=JSON.toJSONString(o);
            sb.append("\n"+js);
        }
        return sb.toString();
    }

    public static String toJson(Object obj) {
        return toJson(obj, false);
    }

    public static String toJson(Object obj, boolean showNull) {
        if (showNull) {
            return JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
        }
        return JSON.toJSONString(obj, SerializerFeature.PrettyFormat);
    }

    // 按行比较,头尾trim,每行trim
    public void equalToLines(String actual, String expected, boolean trim) {
        if (trim) {
            actual = actual.trim();
            expected = expected.trim();
        }
        String[] actuals = actual.split("\n");
        String[] expecteds = expected.split("\n");
        if (actuals.length != expecteds.length) {
            throw new RuntimeException("实际值有" + actuals.length + "行,期望值是" + expecteds.length + "行");
        }
        for (int i = 0; i < actuals.length; i++) {
            if (trim) {
                actuals[i] = actuals[i].trim();
                expecteds[i] = expecteds[i].trim();
            }
            Assert.assertThat(i + ": " + actuals[i].trim(), CoreMatchers.equalTo(i + ": " + expecteds[i].trim()));
        }
    }

    public void equalToFile(String actual, String expectedFilePath, boolean compareByLine) {
        String expected = FileUtil.getText(this.getClass(), expectedFilePath);
        if (!compareByLine) {
            Assert.assertThat(actual, CoreMatchers.equalTo(expected));
        } else {
            equalToLines(actual, expected, true);
        }
    }
}
