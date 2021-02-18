package wang.wangby.codebuilder.utils;

import wang.wangby.exception.Message;
import wang.wangby.utils.StringUtil;

public  enum CodeType{
    model,service,controller,dao,indexPage,insertPage,updatePage;

    //生成的包名
    public String pkgName(String base){
        switch (this){
            case model:
                return base+".model";
            case service:
            case controller:
            case dao:
                return base+"."+this.toString();
        }
        throw new Message("无法获取包名:"+this);
    }

    //生成的类名,不包括包
    public String className(String modelName){
        String name=StringUtil.firstUp(modelName);
        switch (this){
            case model:
                return name;
            case service:
            case controller:
            case dao:
                return name+StringUtil.firstUp(this.name());
        }
        throw new Message("无法获取类名:"+this);
    }

    public String fillName(String base,String model){
        return this.pkgName(base)+"."+this.className(model);
    }
}