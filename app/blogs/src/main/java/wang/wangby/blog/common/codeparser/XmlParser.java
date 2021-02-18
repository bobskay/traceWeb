package wang.wangby.blog.common.codeparser;

import wang.wangby.template.TemplateUtil;

public class XmlParser extends AbstractCodeParser {


    public XmlParser(TemplateUtil templateUtil) {
        super(templateUtil);
    }

    @Override
    public void addWord(StringBuilder sb, String text) {
        text=text.replace("<", "&lt;<span class='tag'$$$$");
        text=text.replace(">", "</span>&gt;");
        text=text.replace("$$$$", ">");
        sb.append(text);
    }
}
