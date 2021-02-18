package wang.wangby.template.velocity;

import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.UberspectImpl;
import org.apache.velocity.util.introspection.VelPropertyGet;
import wang.wangby.template.SimpleObject;

public class MyUberspect extends UberspectImpl {

    public VelPropertyGet getPropertyGet(Object obj, String identifier, Info i)
            throws Exception {

        if (obj == null) {
            return null;
        }

        //扩展原始类型的方法
        if(SimpleObject.isPrimitiveType(obj)){
            return new PrimitiveTypePropertyGet(identifier);
        }
        return super.getPropertyGet(obj,identifier,i);
    }
}
