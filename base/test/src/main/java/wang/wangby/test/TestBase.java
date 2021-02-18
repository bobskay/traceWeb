package wang.wangby.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import wang.wangby.serialize.json.FastJsonImpl;
import wang.wangby.serialize.json.JsonUtil;
import wang.wangby.test.assertutils.MyAssert;
import wang.wangby.test.beanfactory.TestBeanFactory;
import wang.wangby.utils.DateTime;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

public class TestBase {
    protected Logger log= LoggerFactory.getLogger(this.getClass());
    protected  static JsonUtil jsonUtil=new FastJsonImpl();

    static{
        MDC.put("requestId", "test");
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


    public void stringEqual(String result, String expect) {
        MyAssert.stringEqual(result,expect,new Exception("验证字符串相等失败"));
    }

    //将json的输出格式设置为格式化的
    public void setPretty() {
        JSON.DEFAULT_GENERATE_FEATURE = JSON.DEFAULT_GENERATE_FEATURE | SerializerFeature.PrettyFormat.mask;
    }

    //每秒执行一次,直到返回false
    public void perSecond(Callable<Boolean> callable, int max) throws InterruptedException {
        CountDownLatch down = new CountDownLatch(1);
        LongAdder add = new LongAdder();
        new Thread(() -> {
            while (true) {
                try {
                    add.add(1);
                    int i = max - add.intValue();
                    log.debug("准备执行第" + add.intValue() + "次");
                    Boolean end = !callable.call();
                    Thread.sleep(1000);
                    if (i == 0 || end) {
                        break;
                    }
                } catch (Exception e) {
                    log.debug("指定周期任务出错", e);
                    break;
                }
            }
            down.countDown();

        }).start();
        down.await();
    }

    public void perSecond(Callable<Boolean> callable) throws InterruptedException {
        perSecond(callable, 10);
    }



    public <T> T getBean(Class clazz){
        return (T) TestBeanFactory.get(clazz);
    }

    //时间戳
    long beginTime;
    public void timeBegin(){
        beginTime=System.currentTimeMillis();
    }
    public void end(String name){
        String time= DateTime.showTime(System.currentTimeMillis()-beginTime);
        log.debug(name+"耗时:"+time);
    }

    public static void addBean(Object obj) {
        TestBeanFactory.put(obj.getClass(),obj);
    }

    public static void addBean(Class clazz,Object obj) {
        TestBeanFactory.put(clazz,obj);
    }
}
