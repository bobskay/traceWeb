package wang.wangby.template;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TemplateUtilTest {

    @Test
    public void parseText() {
    }

    @Test
    public void parseTemplate() {
        TemplateUtil templateUtil=new TemplateUtil();
        Map map=new HashMap<>();
        map.put("list","1234567".toCharArray());
        map.put("data",new Date());
        map.put("boolValue",false);

        Pojo pojo=new Pojo();
        pojo.setStr("1234567890");
        pojo.setTime(System.currentTimeMillis());
        pojo.setBirthday(new Date());
        pojo.setNo(1234567);
        map.put("pojo",pojo);

        String text=templateUtil.parseTemplate("/test.vm",map);
        log.debug("解析模板结果:\n"+text);
    }
}