package wang.wangby.persistence.file;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class DataSerializer {

    public byte[] toByte(Object obj){
        String s= JSON.toJSONString(obj);
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public <T> T toBean(byte[] bs,Class<T> clazz){
        String s=new String(bs,StandardCharsets.UTF_8).trim();
        return JSON.parseObject(s,clazz);
    }
}
