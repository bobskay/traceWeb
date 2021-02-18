package wang.wangby.blog.model.markdown;

import lombok.Data;

import java.util.List;

@Data
public class Code extends BlogNode {
    private String type;
    private List<String> content;


}
