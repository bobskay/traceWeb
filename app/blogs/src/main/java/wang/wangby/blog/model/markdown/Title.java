package wang.wangby.blog.model.markdown;

import lombok.Data;
import wang.wangby.annotation.Remark;

import java.util.Date;
import java.util.List;

@Data
public class Title extends BlogNode {
    @Remark("标题")
    String title;
    @Remark("创建时间")
    Date createTime;
    @Remark("标签")
    List<String> tags;
    List<String> content;

}
