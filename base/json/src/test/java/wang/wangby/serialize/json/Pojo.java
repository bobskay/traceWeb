package wang.wangby.serialize.json;

import lombok.Data;
import wang.wangby.utils.Dictionary;

import java.util.Date;

@Data
public class Pojo {
    public enum MyDictionary implements Dictionary {
        a1,a2;
    }

    private String str;
    private Date date;
    private Long aLong;
    private MyDictionary dictionary;
}
