package wang.wangby.tools.shell;

import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import wang.wangby.tools.thread.job.JobConfig;
import wang.wangby.tools.thread.job.JobInfo;
import wang.wangby.utils.StringUtil;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ShellClient {
    private static ShellClient INSTANCE = new ShellClient(60*10,"default");

    private Map<String, Shell> map = new ConcurrentHashMap<>();
    private JobInfo jobInfo;
    private long maxIdleSecond;

    public ShellClient(long maxIdleSecond,String name) {
        this.maxIdleSecond=maxIdleSecond;
        JobConfig config=new JobConfig();
        config.setRunnable(this::check);
        config.setName( name+"-ShellClient");
        config.setPeriod((int)maxIdleSecond/10);
    }

    public static ShellClient getDefault(){
        return INSTANCE;
    }

    public void check() {
        map.forEach((key, shell) -> {
            if (System.currentTimeMillis() - shell.getLastTime() > maxIdleSecond*1000) {
                log.info(key+"空闲超过"+maxIdleSecond*1000+",自动移除");
                shell.close();
                map.remove(key);
            }
        });
    }

    public  String exec(String command, String host, String username, String password) throws JSchException, IOException {
        Shell shell=getShell(host,username,password,false);
        String result=null;
        try{
            result= shell.exec(command);
        }catch (JSchException ex){
            //如果关闭了,给一次机会重新后获取链接
            if("channel is not opened".equalsIgnoreCase(ex.getMessage())){
                shell=getShell(host,username,password,true);
                result= shell.exec(command);
            }
        }
        if(StringUtil.isEmpty(result)){
            return "";
        }
        return result;
    }

    public Shell getShell(String host, String username, String password) throws JSchException {
        return getShell(host,username,password,false);
    }


    /**
     * @param force 使用新的client
     * */
    public Shell getShell(String host, String username, String password, boolean force) throws JSchException {
        String key = host + "@" + username + password.replaceAll(StringUtil.REG_LETTER,"*");
        if(!force){
            Shell shell = map.get(key);
            if(shell!=null){
                return shell;
            }
        }

        synchronized (map){
            Shell shell=null;
            if(!force){
                shell=map.get(key);
            }
            if (shell == null) {
                log.info("创建新连接");
                shell = new Shell(host, username, password);
                map.put(key, shell);
            }
            return shell;
        }
    }
}
