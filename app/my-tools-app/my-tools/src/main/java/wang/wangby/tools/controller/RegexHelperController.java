package wang.wangby.tools.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.web.Menu;
import wang.wangby.entity.request.Response;
import wang.wangby.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/regexHelper")
public class RegexHelperController extends ToolController {

    @RequestMapping("/index")
    @Menu("正则表达式测试")
    public String index(String str,String regex) {
        List<RegexResult> list=getResult(str,regex);
        Map map=new HashMap();
        map.put("regexResultList",list);
        map.put("str",str);
        map.put("regex",regex);
        return $("index",map);
    }

    @RequestMapping("test")
    public Response<List<RegexResult>> test(String str, String regex) {
        List list=getResult(str,regex);
        return respone(list);
    }

    private List<RegexResult> getResult(String str,String regex){
        log.debug("验证字符串:"+str+",regex="+regex);
        if(StringUtil.isEmpty(str)|| StringUtil.isEmpty(regex)){
            return new ArrayList<>();
        }
        str=str.trim();
        regex=regex.trim();
        List<RegexResult> list=new ArrayList();
        String[] strings=str.split("\n");
        for(String s:strings){
            String ss=s.trim();
            if(StringUtil.isEmpty(ss)){
                continue;
            }
            RegexResult regexResult=new RegexResult();
            regexResult.str=ss;
            regexResult.regex=regex;
            list.add(regexResult);
        }
        return list;
    }

    @Data
    public class RegexResult{
        private String str;
        private String regex;

        public Boolean isMatch(){
            if(StringUtil.isEmpty(str)|| StringUtil.isEmpty(regex)){
                return false;
            }
            return str.matches(regex);
        }
    }
}
