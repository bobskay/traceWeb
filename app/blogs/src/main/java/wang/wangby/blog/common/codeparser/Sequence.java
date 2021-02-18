package wang.wangby.blog.common.codeparser;

import wang.wangby.blog.model.markdown.Code;
import wang.wangby.utils.IdWorker;
import wang.wangby.utils.StrBuilder;

public class Sequence implements CodeParser {
    @Override
    public void append(StrBuilder sb, Code code) {
        sb.append("<div id='"+ IdWorker.nextString() +"' type='sequence'>");
        for(String st:code.getContent()){
            sb.append(st+"\n");
        }
        sb.append("</div>");
    }
}
