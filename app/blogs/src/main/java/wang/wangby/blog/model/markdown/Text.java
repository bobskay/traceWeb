package wang.wangby.blog.model.markdown;

import lombok.Data;

import java.util.List;

@Data
public class Text extends BlogNode {

    private List<String> content;
    public String getType(){
        return "text";
    }
}
