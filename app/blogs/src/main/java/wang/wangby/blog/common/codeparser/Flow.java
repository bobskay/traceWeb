package wang.wangby.blog.common.codeparser;

import wang.wangby.blog.model.markdown.Code;
import wang.wangby.utils.IdWorker;
import wang.wangby.utils.StrBuilder;

public class Flow implements CodeParser {
    @Override
    public void append(StrBuilder sb, Code code) {
        sb.append("<div id='"+ IdWorker.nextString() +"' type='flow'>");
        for(String st:code.getContent()){
            sb.append(st+"\n");
        }
        sb.append("</div>");
    }
}
