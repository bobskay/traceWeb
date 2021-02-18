package wang.wangby.blog.common.codeparser;

import wang.wangby.template.TemplateUtil;
import wang.wangby.utils.HtmlUtil;

public class DefaultCodeParser extends AbstractCodeParser{

    public DefaultCodeParser(TemplateUtil templateUtil){
        super(templateUtil);
    }

    @Override
    public void addWord(StringBuilder sb, String text) {
        sb.append(HtmlUtil.htmlEscape(text));
    }

}
