package wang.wangby.blog.model.markdown;

import lombok.Data;

import java.util.List;

@Data
public class Table extends BlogNode {
    private String head;
    private String split;
    private List<String> body;


}
