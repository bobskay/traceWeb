package wang.wangby.tools.shell;

import lombok.Data;
import wang.wangby.annotation.Remark;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Remark("shell的执行信息")
@Data
public class ShellExecInfo {
    private String pid;
    private Queue<String> datas;
    private long lastRead;//最后一次读取数据时间,如果用户长时间没来读书节,就会自动关闭
    private boolean daemon;//需要手动关闭
    private String command;
    private Shell shell;
    private String key;


    List<String> poll() {
        lastRead = System.currentTimeMillis();
        List list = new ArrayList();
        while (true) {
            String line = datas.poll();
            if (line == null) {
                return list;
            }
            list.add(line);
        }
    }

    boolean push(String result) {
        return datas.offer(result);
    }

}
