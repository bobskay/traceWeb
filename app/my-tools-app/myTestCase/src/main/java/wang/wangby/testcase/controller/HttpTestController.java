package wang.wangby.testcase.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.api.Param;
import wang.wangby.annotation.web.Menu;
import wang.wangby.exception.Message;
import wang.wangby.entity.request.Response;
import wang.wangby.utils.DateTime;
import wang.wangby.utils.IdWorker;
import wang.wangby.utils.StrBuilder;
import wang.wangby.utils.StringUtil;
import wang.wangby.web.controller.BaseController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("testCase/httpTest")
@RestController
@Slf4j
@Menu("测试")
public class HttpTestController extends BaseController {
    private volatile Map<String,Long> runningThreads =new ConcurrentHashMap<>();


    @Menu("http测试")
    @RequestMapping("index")
    public String index(){
        Map map=new HashMap(runningThreads);
        Map ret=new HashMap();
        ret.put("runningThreads",map);
        return $("index",ret);
    }

    @Menu("restfull请求")
    @RequestMapping("restfull")
    public String restfull(){
        return $("restfull");
    }


    @Menu("模拟请求")
    @RequestMapping("invokeIndex")
    public String invokeIndex(){
        return $("invokeIndex");
    }



    @Remark("模拟耗时调用")
    @RequestMapping("invoke")
    @Param("最短耗时,默认0")
    @Param("最长耗时,默认1000")
    public Response<Long> invoke(Integer min, Integer max) throws InterruptedException {
        long time=getSleep(min,max);
        Thread.sleep(time);
        return respone(time);
    }

    //获取睡眠时间
    long getSleep(Integer min,Integer max){
        if(min==null){
            min=0;
        }
        if(max==null){
            max=10;
        }
        if(max<min){
            return 0;
        }
        int range = max - min;
        if(range==0){
            return max;
        }
        Random rd = new Random();
        return rd.nextInt(range)+min;
    }

    @RequestMapping("error")
    public Response<String> error(String type) throws Exception{
        if("message".equalsIgnoreCase(type)){
            throw new Message("message异常:"+type);
        }
        throw new Exception("未知异常:"+type);
    }

    @RequestMapping("errorHtml")
    public String errorHtml(String type) throws Exception{
        if("message".equalsIgnoreCase(type)){
            throw new Message("mesage异常:"+type);
        }
        throw new Exception("未知异常:"+type);
    }

    @RequestMapping("postBody")
    public JSONObject string(@RequestBody String body){
        log.debug("收到测试数据:"+body);
        JSONObject obj=JSON.parseObject(body);
        return obj;
    }


    @Remark("超时调用,一直超时直到另一个线程过来调用killInvoke")
    @RequestMapping("timeout")
    public Response<String> timeout(String key) throws InterruptedException {
        long begin=System.currentTimeMillis();
        if(StringUtil.isEmpty(key)){
            key= IdWorker.nextString();
        }
        runningThreads.put(key,System.currentTimeMillis());

        while(runningThreads.containsKey(key)){
            Thread.sleep(100);
        }
        long time=System.currentTimeMillis()-begin;
        return respone("总耗时:"+DateTime.showTime(time));
    }

    @RequestMapping("kill")
    public Response<String> kill(String key){
       Long result= runningThreads.remove(key);
       if(result!=null){
           long time=System.currentTimeMillis()-result;
           return respone("共执行了:"+DateTime.showTime(time));
       }
       return respone("已经移除");
    }

    @Remark("获得所有正在超时的调用")
    @RequestMapping("timeoutKeys")
    public Response<List<String>> timeoutKeys(){
        List list=new ArrayList(runningThreads.keySet());
        return respone(list);
    }

    @RequestMapping("requestInfo")
    public String requestInfo(HttpServletRequest request){
        StrBuilder sb=new StrBuilder();
        sb.appendLine("-----------------heads-------------------");
        Enumeration<String>  heads=  request.getHeaderNames();
        while(heads.hasMoreElements()){
            String key=heads.nextElement();
            String value=request.getHeader(key);
            sb.appendLine(key+":"+value);
        }

        sb.appendLine("\n---------------parameter-------------------");
        request.getParameterMap().forEach((key,vs)->{
            sb.appendLine(key+":"+Arrays.asList(vs));
        });
        if(request.getCookies()!=null){
            sb.appendLine("cookie-------------------");
            for (Cookie ck :request.getCookies()){
                sb.appendLine(jsonUtil.toString(ck));
            }
        }
        sb.appendLine("-----------------------------------");
        sb.appendLine("method:"+request.getMethod());
        sb.appendLine("authType:"+request.getAuthType());
        sb.appendLine("protocol:"+request.getProtocol());
        sb.appendLine("-----------------------------------");
        sb.appendLine("contentType:"+request.getContentType());
        sb.appendLine("contextPath:"+request.getContextPath());
        sb.appendLine("servletPath:"+request.getServletPath());
        sb.appendLine("requestUri:"+request.getRequestURI());
        sb.appendLine("---------------------------------------");
        sb.appendLine("servletContext:"+request.getServletContext());
        sb.appendLine("serverPort:"+request.getServerPort());
        sb.appendLine("serverName:"+request.getServerName());

        return "<pre>"+sb.toString();
    }
}
