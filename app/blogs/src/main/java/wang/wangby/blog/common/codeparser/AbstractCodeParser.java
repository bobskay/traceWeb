package wang.wangby.blog.common.codeparser;

import wang.wangby.blog.common.Blogs;
import wang.wangby.blog.common.codeparser.vo.StringItem;
import wang.wangby.blog.model.markdown.Code;
import wang.wangby.template.TemplateUtil;
import wang.wangby.utils.FileUtil;
import wang.wangby.utils.StrBuilder;

import java.util.*;

abstract public class AbstractCodeParser implements CodeParser {
    private String codeTemplate;
    private TemplateUtil templateUtil;

    public AbstractCodeParser(TemplateUtil templateUtil){
        this.templateUtil=templateUtil;
    }

    @Override
    public void append(StrBuilder sb, Code code) {
        Map map=new HashMap();
        List<String> content=code.getContent();
        List<String> rows=new ArrayList<>();
        List<String> codes=new ArrayList<>();
        for(int i=0;i<content.size();i++){
            rows.add((i+1)+"");
            String text=content.get(i);
            text=formatLine(text);
            codes.add(text);
        }
        map.put("rows",rows);
        map.put("codes",codes);
        map.put("type",code.getType());
        String codeTemplate= getCodeTemplate();
        String codeTxt=templateUtil.parseText(codeTemplate,map);
        sb.append(codeTxt);
    }

    public String formatLine(String text) {
        List<StringItem> items = new ArrayList<>();
        String remain = toStringItem(text, items);
        StringBuilder sb = new StringBuilder();
        for (StringItem item : items) {
            addWord(sb, item.getPrefix());
            sb.append("<span class='string'>" + item.getSplit() + item.getContent() + item.getSplit() + "</span>");
        }
        addWord(sb, remain);
        return sb.toString();
    };

    abstract  public void addWord(StringBuilder sb,String text);

    protected String getCodeTemplate() {
        return FileUtil.getText(Blogs.class,"/templates/blog/code.html");
    }

    protected Set<String> getKeyword(String type){
        String text=FileUtil.getText(Blogs.class,"/keywords/"+type+"/keyword.txt");
        Set set=new HashSet();
        for(String word:text.split("\\s+")){
           set.add(word);
        }
        return set;
    }


    String toStringItem(String text, List<StringItem> items) {
        int a = text.indexOf('\'');
        int b = text.indexOf('"');
        if (a == -1) {
            return toStringItem(text, '"', items);
        }
        if (b == -1) {
            return toStringItem(text, '\'', items);
        }
        if (a > b) {
            String remain = toStringItem(text, '"', items);
            return toStringItem(remain, '\'', items);
        } else {
            String remain = toStringItem(text, '\'', items);
            return toStringItem(remain, '"', items);
        }
    }

    String toStringItem(String text, char split, List<StringItem> items) {
        if (text.indexOf(split) == -1) {
            return text;
        }
        char[] chars = text.toCharArray();
        StringBuilder prefix = new StringBuilder();
        StringBuilder content = new StringBuilder();
        StringItem item = null;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == split) {
                //第一个字符或者前一个字符不是\说明字符串开始了
                if (i == 0 || chars[i - 1] != '\\') {
                    if (item == null) {
                        item = new StringItem();
                        item.setPrefix(prefix.toString());
                        item.setSplit(split);
                        continue;
                    } else {
                        //第二次遇到说明字符串结束
                        item.setContent(content.toString());
                        items.add(item);
                        return toStringItem(new String(chars, i + 1, chars.length - i - 1), items);
                    }
                }
            }
            if (item == null) {
                prefix.append(chars[i]);
            } else {
                content.append(chars[i]);
            }
        }
        if (item == null) {
            return text;
        }
        item.setContent(content.toString());
        items.add(item);
        //全部循环完还没遇到结束符说明只包含一个双引号
        return "";
    }

}
