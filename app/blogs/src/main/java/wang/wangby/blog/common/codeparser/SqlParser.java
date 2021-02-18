package wang.wangby.blog.common.codeparser;

import wang.wangby.template.TemplateUtil;
import wang.wangby.utils.HtmlUtil;
import wang.wangby.utils.StringPicker;

import java.util.Set;

public class SqlParser extends AbstractCodeParser {

    private Set<String> keywords;

    public SqlParser(TemplateUtil templateUtil) {
        super(templateUtil);
        keywords = super.getKeyword("sql");
    }

    @Override
    public void addWord(StringBuilder sb, String text) {
        StringPicker picker = new StringPicker(text);
        while (true) {
            StringBuilder split = new StringBuilder();
            String next = picker.next(" ".toCharArray(), split);
            if (split.length() == 0 && next.length() == 0) {
                String remain = picker.nextAll();
                sb.append(HtmlUtil.htmlEscape(remain));
                return;
            }

            if (next.startsWith("#")) {
                sb.append("<span class='comment'>" + next + split + picker.nextAll() + "</span>");
                return;
            }

            String end = HtmlUtil.htmlEscape(split.toString());
            if (keywords.contains(next.toUpperCase())) {
                sb.append("<span class='keyword'>" + next + "</span>");
                sb.append(end);
                continue;
            }

            sb.append(HtmlUtil.htmlEscape(next));
            sb.append(end);
        }
    }
}
