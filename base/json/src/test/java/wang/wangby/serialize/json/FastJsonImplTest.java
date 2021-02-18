package wang.wangby.serialize.json;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

@Slf4j
public class FastJsonImplTest {

    @Test
    public void toString1() {
        Pojo pojo=new Pojo();
        pojo.setALong(1234567890123456789L);
        pojo.setDate(new Date());
        pojo.setDictionary(Pojo.MyDictionary.a1);
        pojo.setStr("this is a str");
        FastJsonImpl json=new FastJsonImpl();
        log.debug(json.toString(pojo));

        log.debug("修改MyDictionary的序列化方式");
        Map map=FastJsonImpl.defaultSerializeConfig();
        map.put(Pojo.MyDictionary.class, DictionarySerializer.instance);
        FastJsonImpl.updatGlobalConfig(map,null);
        log.debug(json.toFormatString(pojo));
    }
}