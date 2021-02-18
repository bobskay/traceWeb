package wang.wangby.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.springframework.beans.factory.annotation.Autowired;
import wang.wangby.test.beanfactory.TestBeanFactory;
import wang.wangby.utils.ClassUtil;

import java.lang.reflect.Field;

public class MyTestRunner extends BlockJUnit4ClassRunner {
    public MyTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    protected Object createTest() throws Exception {
        Object test=super.createTest();
        for(Field f: ClassUtil.getFieldsByAnnotation(test.getClass(), Autowired.class)){
            f.setAccessible(true);
            Object value=f.get(test);
            if(value!=null){
                continue;
            }
            f.set(test,TestBeanFactory.get(f.getType()));
        }
        return test;
    }
}
