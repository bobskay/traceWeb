package wang.wangby.blog.common.codeparser;

import wang.wangby.blog.model.markdown.Code;
import wang.wangby.utils.StrBuilder;

public interface  CodeParser {
    void append(StrBuilder sb,Code code);
}
