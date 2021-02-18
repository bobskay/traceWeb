package wang.wangby.blog.model.markdown;

import lombok.Data;

import java.util.List;

@Data
public class MdContent  {
    private String type;
    private List<BlogNode> blogNodes;
}
