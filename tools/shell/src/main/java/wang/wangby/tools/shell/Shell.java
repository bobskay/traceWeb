package wang.wangby.tools.shell;

import com.jcraft.jsch.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wang.wangby.utils.StrBuilder;
import wang.wangby.utils.StringPicker;
import wang.wangby.utils.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Shell implements AutoCloseable {
    public static final int DEFAULT_PORT = 22;
    @Getter
    private long lastTime;//最后执行时间
    private Session session;

    public Shell(String host, String username, String password) throws JSchException {
        this(host, DEFAULT_PORT, username, password);
    }

    public Shell(String host, int port, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(username, host, port);
        session.setPassword(password);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
    }

    public void upload(String src, String dst) throws JSchException, SftpException {
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
        channelSftp.put(src, dst);
        channelSftp.quit();
    }


    /*  提取出 22327
        root     22327 22323  0 17:45 ?        00:00:00 bash -c tail -f /opt/logtask/logs/1.txt 2>&1
        root     22328 22323  0 17:45 ?        00:00:00 bash -c ps -ef|grep "tail -f /opt/logtask/logs/1.txt 2>&1" 2>&1
        root     22344 22328  0 17:45 ?        00:00:00 grep tail -f /opt/logtask/logs/1.txt 2>&1
     */
    public List<String> getPid(String command) throws IOException, JSchException {
        command=command.trim().replaceAll(StringUtil.REG_BLANK," ");
        String execCommand = command.trim().replace("\"", "\"\\\"");
        String psInfo = this.exec("ps -ef|grep \"" + execCommand + "\"");
        List<String> pids=new ArrayList<>();
        for (String str : psInfo.split("\n")) {
            str = str.replaceAll(StringUtil.REG_BLANK, " ").trim();
            //忽略不是以当前命令结尾的
            if (!str.endsWith(command)) {
                continue;
            }
            //忽略grep
            if (str.endsWith("grep " + command)) {
                continue;
            }
            StringPicker pk = new StringPicker(str.trim());
            pk.next(" ");//root
            String pid = pk.next(" ");
            pids.add(pid);
        }
        if(pids.size()==0){
            log.debug("找不到命令对应的pid:"+psInfo);
        }
        return pids;
    }

    public String kill(String pid) throws IOException, JSchException {
        return this.exec("kill " + pid);
    }

    //运行某个命令,
    public void run(String command, ShellOutput out) throws JSchException, IOException {
        command=command.trim().replaceAll(StringUtil.REG_BLANK," ");
        log.debug("准备执行命令:"+command);
        lastTime = System.currentTimeMillis();
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.connect();
        InputStream in = channel.getInputStream();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if(out!=null){
                    out.out(line);
                }else{
                    break;
                }
            }
        }
    }

    //执行shell
    public String exec(String command) throws JSchException, IOException {
        final StrBuilder sb = new StrBuilder();
        ShellOutput strOut = new ShellOutput() {
            public void out(String line) {
                sb.appendLine(line);
            }
        };
        this.run(command, strOut);
        String str = sb.toString();
        if (log.isTraceEnabled()) {
            log.trace(command + "返回内容:" + StringUtil.subString(str, 1000));
        }
        return str;
    }

    //通过端口号获得pid
    public String getPid(Integer port) throws IOException, JSchException {
        String command="lsof -i:"+port;
        String out=this.exec(command).trim();
        if(StringUtil.isEmpty(out)){
            return null;
        }
        StringPicker pk=new StringPicker(out);
        //第一行是标题
        pk.next("\n");
       String message=pk.next("\n");
       //获取第二列
        pk=new StringPicker(message.replaceAll(StringUtil.REG_BLANK," "));
        pk.next(" ");
        return pk.next(" ");
    }

    public void close() {
        session.disconnect();
    }
}