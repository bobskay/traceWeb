package wang.wangby.tools.shell.file;

import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import wang.wangby.tools.shell.Shell;
import wang.wangby.utils.StringUtil;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

@Slf4j
public class RemoteFile {
    private Shell shell;

    public RemoteFile(String host, String username, String password) throws JSchException {
        log.debug("准备访问服务器:host={},username={}",host,username);
        shell = new Shell(host, username, password);
    }

    public RemoteFile(Shell shell) throws JSchException {
        this.shell=shell;
    }

    //1.txt 6 1 81f8 0 981 27 126 1 0 0 1571564147 1571564325 1571564325 0 4096 system_u:object_r:vmblock_t:s0
    public FileInfo getFileInfo(String fileName) throws IOException, JSchException {
        String command = "stat -t " + fileName;
        String result = shell.exec(command);
        return statStrToFileInfo(result);
    }

    private FileInfo statStrToFileInfo(String result) {
        String[] data = result.trim().split(" ");
        if (data.length != 17) {
            log.error("返回数据格式不正确:" + result);
            return null;
        }
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(data[0]);
        fileInfo.setSize(Long.parseLong(data[1]));
        fileInfo.setAccess(new Date(Long.parseLong(data[11]) * 1000L));
        fileInfo.setModify(new Date(Long.parseLong(data[12]) * 1000L));
        fileInfo.setChange(new Date(Long.parseLong(data[13]) * 1000L));
        return fileInfo;
    }

    public String[] ls(String file) throws IOException, JSchException {
        String command = "ls " + file;
        String result = shell.exec(command);
        return result.trim().split(StringUtil.REG_BLANK);
    }

    //通过目录查找文件,用户通过文件名判断文件是否符合规则,符合规则的再查询详细信息
    public List<FileInfo> getFileInfo(String dir, Function<String,Boolean> isMatch) throws IOException, JSchException {
        if(dir.endsWith("/")){
            dir=dir.substring(0,dir.length()-1);
        }
        String[] files=ls(dir);
        Set<String> fileNames=new HashSet<>();
        for(String f:files){
            if(isMatch.apply(f)){
                fileNames.add(dir+"/"+f);
            }
        }
        return  getFileInfo(fileNames);
    }

    //获取一组文件信息
    public List<FileInfo> getFileInfo(Set<String> files) throws IOException, JSchException {
        if(files.size()==0){
            return new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();
        for (String fileName : files) {
            sb.append(fileName.replaceAll(StringUtil.REG_BLANK, "") + " ");
        }
        System.out.println(sb);
        String command = "stat -t " + sb.toString();
        String result = shell.exec(command);
        String[] datas = result.split("\n");
        List list = new ArrayList();
        for (String data : datas) {
            FileInfo file = statStrToFileInfo(data);
            if (file != null) {
                list.add(file);
            }
        }
        return list;
    }

    //按字节读取文件
    public String read(String fileName, Long begin, Integer size) throws IOException, JSchException {
        String command="tail -c +"+begin+" "+fileName+"|head -c "+size;
        return shell.exec(command);
    }
}
