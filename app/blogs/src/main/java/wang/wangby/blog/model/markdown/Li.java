package wang.wangby.blog.model.markdown;

import lombok.Data;

import java.util.List;

@Data
public class Li {
    private String prefix;
    private List<String> content;
}
