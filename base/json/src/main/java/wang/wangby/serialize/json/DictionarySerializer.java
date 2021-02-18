package wang.wangby.serialize.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import wang.wangby.utils.Dictionary;
import wang.wangby.utils.EnumUtil;

import java.io.IOException;
import java.lang.reflect.Type;

public class DictionarySerializer implements ObjectSerializer, ObjectDeserializer {
    public static final DictionarySerializer instance = new DictionarySerializer();

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
            return;
        }
        Dictionary dictionary = (Dictionary) object;
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"key\"");
        sb.append(":");
        sb.append("\"" + dictionary.getKey() + "\"");
        sb.append(",");
        sb.append("\"value\"");
        sb.append(":");
        sb.append("" + dictionary.getValue() + "");
        sb.append("}");
        out.write(sb.toString().toCharArray());
    }

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        if(type instanceof Dictionary){
            return null;
        }

        String keyOrValue = parser.parseObject(String.class);
        if (keyOrValue != null && keyOrValue.startsWith("{")) {
            JSONObject map = JSON.parseObject(keyOrValue);
            keyOrValue = map.getString("key");
        }
        Enum[] enums = EnumUtil.getEnums((Class) type);
        for (Enum e : enums) {
            Dictionary d = (Dictionary) e;
            if (d.getKey().equals(keyOrValue)) {
                return (T) e;
            }
            if (d.getValue().equals(keyOrValue)) {
                return (T) e;
            }
        }
        return null;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
