package wang.wangby.tools.shell.file;

import lombok.Data;

import java.util.Date;

@Data
public class FileInfo {
    private Long size;
    private Date access;
    private Date modify;//修改文件内容
    private Date change;//修改文件包括权限变化
    private String fileName;

}
