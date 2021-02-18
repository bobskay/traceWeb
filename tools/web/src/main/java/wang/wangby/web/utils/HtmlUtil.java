package wang.wangby.web.utils;

import wang.wangby.serialize.json.JsonUtil;
import wang.wangby.utils.BeanUtil;
import wang.wangby.utils.Dictionary;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class HtmlUtil {

    JsonUtil jsonUtil;
    public HtmlUtil(JsonUtil jsonUtil){
        this.jsonUtil=jsonUtil;
    }

    public <T> String select(String name, List<T> list, Function<T,String> getKey,Function<T,String> getText){
        StringBuilder sb=new StringBuilder();
        sb.append("<select class='selectpicker' id='"+name+"' name='"+name+"'>");
        for(T t:list){
            sb.append("<option value='"+getKey.apply(t)+"'>"+getText.apply(t)+"</option>");
        }
        sb.append("	</select>");
        System.out.println(sb.toString());
        return sb.toString();
    }

    //
    public <T> String select(String name, List<T> list, String keyField,String textField){
        Function<T,String> getKey=t->{
            return BeanUtil.get(t,keyField)+"";
        };
        Function<T,String> getText=t->{
            return BeanUtil.get(t,textField)+"";
        };
        return select(name,list,getKey,getText);
    }

    public String select(String name, Dictionary[] list){
        return select(name,Arrays.asList(list));
    }

    public String select(String name, List<Dictionary> list){
        Function<Dictionary,String> getKey=t->{
            return t.getKey();
        };
        Function<Dictionary,String> getText=t->{
            return t.getText();
        };
        return select(name,list,getKey,getText);
    }

    public String json(Object o){
        return jsonUtil.toString(o);
    }

    public Integer add(Integer value,Integer add){
        return value+add;
    }
}
