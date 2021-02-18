package wang.wangby.codebuilder.controller.vo;

import lombok.Data;
import wang.wangby.annotation.Remark;
import wang.wangby.utils.StringUtil;

@Data
public class CodeConfig {
    private String sql;
    @Remark("要生成代码所在包名")
    private String packageName;
    @Remark("主菜单名")
    private String menuName;
    private String hostname;
    private String username;
    private String password;
    private String workdir;
    private Datasource datasource;
    @Remark("模块名")
    private String modelName;
    @Remark("项目名称")
    private String appName;
    @Remark("文件生成目录")
    private String outputDir;
    @Remark("项目类型")
    private String projectType;
    @Remark("启动端口")
    private String serverPort;

    private DubboConfig dubbo=new DubboConfig();

    @Data
    public static class Datasource{
        private String username;
        private String password;
        private String url;
    }

    public String getUpModelName(){
        if(modelName==null){
            return null;
        }
        return StringUtil.firstUp(modelName);
    }

    public String getUpAppName(){
        if(appName==null){
            return null;
        }
        return StringUtil.firstUp(appName);
    }

    public String getPackageDir(){
        if(packageName==null){
            return null;
        }
        return packageName.replace('.','/');
    }


    @Data
    public static class DubboConfig{
        private String registryAddress;
    }
}
