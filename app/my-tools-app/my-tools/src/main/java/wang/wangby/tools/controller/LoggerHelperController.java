package wang.wangby.tools.controller;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.web.Menu;
import wang.wangby.log.LogLevel;
import wang.wangby.entity.request.Response;
import wang.wangby.tools.controller.vo.AceTip;
import wang.wangby.utils.CollectionUtil;
import wang.wangby.utils.StringUtil;

import java.util.*;

@RestController
@RequestMapping("/loggerHelper")
public class LoggerHelperController extends ToolController {

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping("/index")
    @Menu("修改日志级别")
    public String index() {
        List<AceTip> tips=new ArrayList<>();
        Set<String> set=new HashSet();
        //获取系统所有bean
        for(Object o:applicationContext.getBeansOfType(Object.class).values()){
            Class cls=formatClass(o.getClass());
            String name=cls.getName();
            if(set.contains(name)){
                continue;
            }
            set.add(name);
            AceTip clazz=new AceTip();
            clazz.setCaption(cls.getSimpleName());
            clazz.setMeta("class");
            clazz.setName(name);
            clazz.setValue(name);
            tips.add(clazz);
        }
        Map map=new HashMap();
        map.put("aceTips",jsonUtil.toString(tips));
        return $("index",map);
    }

    @RequestMapping("/getLogger")
    public Response<List<Logger>> getLogger(String name) throws Exception {
        List list=findLogger(name);
        if(list.size()==0){
            Logger logger= LoggerFactory.getLogger(Class.forName(name));
            ch.qos.logback.classic.Logger log=(ch.qos.logback.classic.Logger)logger;
            list.add(log);
        }
        return respone(list);
    }


    @RequestMapping("/changeLevel")
    public Response<String> changeLevel(String name, String level) throws Exception {
        try{
            Class clazz=Class.forName(name);
            Level lv= (Level)Level.class.getField(level.toUpperCase()).get(Level.class);
            LogLevel.changeLevel(clazz,lv);
            return respone("ok");
        }catch(ClassNotFoundException ex){
            List<ch.qos.logback.classic.Logger> list=findLogger(name);
            Level lv= (Level)Level.class.getField(level.toUpperCase()).get(Level.class);
            for(ch.qos.logback.classic.Logger lg:list){
                lg.setLevel(lv);
            }
            return respone("ok");
        }

    }

    private List<ch.qos.logback.classic.Logger> findLogger(String name){
        List< ch.qos.logback.classic.Logger> list=new ArrayList();
        for(Object o:applicationContext.getBeansOfType(Object.class).values()) {
            Class cls = formatClass(o.getClass());
            if(cls.getName().startsWith("java")){
                continue;
            }
            if(cls.getName().startsWith("com.sun")){
                continue;
            }
            if(cls.getName().startsWith(name)){
                Logger logger= LoggerFactory.getLogger(cls);
                ch.qos.logback.classic.Logger log=(ch.qos.logback.classic.Logger)logger;
                if(StringUtil.isEmpty(log.getName())){
                    continue;
                }
                list .add(log);
            }

        }
        CollectionUtil.sort(list,ch.qos.logback.classic.Logger::getName);
        return list;
    }

    private Class formatClass(Class clazz){
        if(clazz.getName().indexOf("$")!=-1){
            return clazz.getSuperclass();
        }
        return clazz;
    }
}
