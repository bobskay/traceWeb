package wang.wangby.blog.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import wang.wangby.blog.common.codeparser.*;
import wang.wangby.blog.model.markdown.*;
import wang.wangby.template.TemplateUtil;
import wang.wangby.utils.FileUtil;
import wang.wangby.utils.StrBuilder;
import wang.wangby.utils.StringUtil;

import java.util.*;

@Data
@Slf4j
public class Blogs {
    private Map<String, CodeParser> codeParserMap;
    private Map<String, Blog> blogMap;
    private TemplateUtil templateUtil;
    private String codeTemplate;
    private DefaultCodeParser defaultCodeParser;

    public Blogs(List<Blog> blogs, TemplateUtil templateUtil) {
        blogMap = new TreeMap();
        for (Blog blog : blogs) {
            blogMap.put(blog.getId(), blog);
        }
        this.templateUtil = templateUtil;

        SqlParser sql = new SqlParser(templateUtil);
        XmlParser xml = new XmlParser(templateUtil);
        codeParserMap = new HashMap();
        codeParserMap.put("flow", new Flow());
        codeParserMap.put("sequence", new Sequence());
        codeParserMap.put("java", new JavaParse(templateUtil));
        codeParserMap.put("xml", xml);
        codeParserMap.put("html", xml);
        codeParserMap.put("sql", sql);
        codeParserMap.put("mysql", sql);
        defaultCodeParser = new DefaultCodeParser(templateUtil);
    }


    public String toHtml(Blog blog) {
        StrBuilder sb = new StrBuilder();
        appendBody(sb, blog.getSummary());
        appendBody(sb, blog.getBody());
        return sb.toString();
    }

    private void appendBody(StrBuilder sb, MdContent content) {
        if (content == null || content.getBlogNodes() == null) {
            return;
        }
        for (BlogNode node : content.getBlogNodes()) {
            if (node instanceof Text) {
                appendText(sb, (Text) node);
            } else if (node instanceof Code) {
                appendCode(sb, (Code) node);
            } else if (node instanceof Table) {
                appendTable(sb, (Table) node);
            } else if (node instanceof LiGroup) {
                appendList(sb, (LiGroup) node);
            }
        }
    }

    private void appendList(StrBuilder sb, LiGroup node) {
        Map map = new HashMap();
        for (Li li : node.getLiList()) {
            List newContent = new ArrayList();
            if (li.getPrefix().matches(StringUtil.REG_INTEGER)) {
                newContent.add(li.getPrefix());
            }
            for (String content : li.getContent()) {
                String format=formatText(content);
                if(format==null){
                    newContent.add(content);
                }else{
                    //如果不是h标签就加一个换行
                    if(!format.startsWith("<h")){
                        format+="<br>";
                    }
                    newContent.add(format);
                }
            }
            li.setContent(newContent);
        }
        map.put("liGroup", node);
        String template = FileUtil.getText(Blogs.class, "/templates/blog/ol.html");
        String codeTxt = templateUtil.parseText(template, map);
        sb.append(codeTxt);
    }

    private void appendText(StrBuilder sb, Text node) {
        for (String text : node.getContent()) {
            String format=formatText(text);
            if(format!=null){
                sb.append(format);
            }else{
                sb.append("<p>"+text+"</p>");
            }

        }
    }

    private String formatText(String text) {
        if (text.startsWith("####")) {
            return ("<h4>" + text.substring(4) + "</h4>");
        }
        if (text.startsWith("###")) {
            return ("<h3>" + text.substring(3) + "</h3>");
        }
        if (text.startsWith("##")) {
            return ("<h2>" + text.substring(2) + "</h2>");
        }
        if (text.startsWith("#")) {
            return ("<h1>" + text.substring(1) + "</h1>");
        }
        if (text.startsWith("![")) {
            String url = StringUtil.getFirstAfter(text, "(");
            url = StringUtil.getLastBefore(url, ")");
            return ("<img class='blogImg' src='" + url + "'/>");
        }
        return null;
    }

    private void appendTable(StrBuilder sb, Table table) {
        sb.append("<table>");
        sb.append("<tr>");
        String[] td = table.getHead().split("\\|");
        for (int i = 1; i < td.length; i++) {
            sb.append("<th>" + td[i] + "</th>");
        }
        sb.append("</tr>");
        for (String line : table.getBody()) {
            sb.append("<tr>");
            String[] dd = line.split("\\|");
            for (int i = 1; i < dd.length; i++) {
                sb.append("<td>" + dd[i] + "</td>");
            }
            sb.append("</tr>");
        }
        sb.appendLine("</table>\n");
    }

    //显示代码
    private void appendCode(StrBuilder sb, Code code) {
        CodeParser parser = codeParserMap.get(code.getType());
        if (parser != null) {
            parser.append(sb, code);
            return;
        }
        defaultCodeParser.append(sb, code);
    }
}
