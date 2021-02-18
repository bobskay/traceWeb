package wang.wangby.blog.model.markdown;

import lombok.Data;
import wang.wangby.annotation.Remark;

@Data
public class Blog {
    @Remark("文件信息")
    private Title title;
    @Remark("概述")
    MdContent summary;
    @Remark("内容")
    MdContent body;
    @Remark("对应md文件为位置")
    String absolutePath;
    @Remark("所在目录")
    String category;
    @Remark("文件名")
    String fileName;
    @Remark("博客标识")
    private String id;

}
