package wang.wangby.blog.model.markdown;

import lombok.Data;

import java.util.List;

@Data
public class LiGroup extends BlogNode {
    private List<Li> liList;
}

