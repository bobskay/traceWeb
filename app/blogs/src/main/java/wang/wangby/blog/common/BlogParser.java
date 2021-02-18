package wang.wangby.blog.common;

import lombok.extern.slf4j.Slf4j;
import wang.wangby.blog.model.markdown.*;
import wang.wangby.utils.FileUtil;
import wang.wangby.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class BlogParser {
    public static final String TABLE_FLAG = "|";
    public static final String BODY_SPLIT = "<!--more-->";
    public static final String CODE = "```";
    public static final String TITLE_FLAG = "---";
    public static final String NUMBER_LIST = "^\\d+[.][ ].*";
    public static final String[] LIST = new String[]{"*", "-"};
    private Blog blog;
    private File markdown;

    public BlogParser(File markdown) {
        this.markdown = markdown;
        doParse();
    }

    public static List<Blog> getAll(String markdownDir) {
        List<Blog> list = new ArrayList();
        File root = new File(markdownDir);
        String formatRoot = FileUtil.format(root.getAbsolutePath());
        FileUtil.iterator(root, (dir, file) -> {
            log.debug("准备解析:" + file.getAbsolutePath());
            if (!file.getName().endsWith(".md")) {
                return true;
            }
            BlogParser parser = new BlogParser(file);
            try {

                Blog blog = parser.getBlog();

                String fileName =FileUtil.format(file.getAbsolutePath());
                fileName=fileName.replace(formatRoot, "");
                String blogDir = StringUtil.getLastBefore(fileName, "/");
                String blogName = StringUtil.getLastAfter(fileName, "/");
                blogName = StringUtil.getLastBefore(blogName, ".");

                blog.setCategory(blogDir);
                blog.setFileName(blogName);
                blog.setId(blog.getCategory() + "/" + blog.getFileName());
                if (blog.getTitle() == null) {
                    blog.setTitle(new Title());
                }
                if (StringUtil.isEmpty(blog.getTitle().getTitle())) {
                    blog.getTitle().setTitle(blog.getFileName());
                }

                list.add(parser.getBlog());
            } catch (Exception ex) {
                log.error("解析文件出错跳过:" + file.getAbsolutePath(), ex);
            }
            return true;
        });
        return list;
    }

    public Blog getBlog() {
        return blog;
    }

    private void doParse() {
        String text = FileUtil.getText(markdown.getAbsolutePath());
        String[] lines = text.split("\r*\n");
        blog = new Blog();
        blog.setAbsolutePath(markdown.getAbsolutePath());
        addTitle(lines);
    }

    private void addSummary(int i, String[] lines) {
        MdContent summary = new MdContent();
        summary.setBlogNodes(new ArrayList<>());
        summary.setType("summary");
        blog.setSummary(summary);
        addBody(i, lines, summary);
    }

    private void addBody(int i, String[] lines, MdContent body) {
        if (i >= lines.length) {
            return;
        }
        if (lines[i].startsWith(BODY_SPLIT)) {
            blog.setBody(new MdContent());
            blog.getBody().setBlogNodes(new ArrayList<>());
            blog.getBody().setType("body");
            body = blog.getBody();
        } else if (lines[i].startsWith(CODE)) {
            i = addCode(i, lines, body);
        } else if (lines[i].startsWith(TABLE_FLAG)) {
            i = addTable(i, lines, body);
        } else if (lines[i].matches(NUMBER_LIST)) {
            i = addList(i, lines, body, str -> {
                Li li = new Li();
                li.setContent(new ArrayList<>());
                String prefix = StringUtil.getFirstBefore(str, ".");
                String content = StringUtil.getFirstAfter(str, ".").replaceAll("^\\s+", "");//leftTrim
                li.setPrefix(prefix);
                li.getContent().add(content);
                return li;
            }, str -> str.matches(NUMBER_LIST));
        } else {
            String flag = isList(lines[i]);
            if (flag != null) {
                i = addList(i, lines, body, str -> {
                    Li li = new Li();
                    li.setContent(new ArrayList<>());
                    String content = StringUtil.getFirstAfter(str, flag).replaceAll("^\\s+", "");//leftTrim
                    li.setPrefix(flag);
                    li.getContent().add(content);
                    return li;
                }, str -> isList(str) != null);
            } else {
                i = addContent(i, lines, body);
            }
        }
        addBody(i + 1, lines, body);
    }

    public String isList(String line) {
        for (String s : LIST) {
            if(line.startsWith(s + " ")){
                return s;
            }
            if(line.startsWith(" "+s + " ")){
                return s;
            }
            if(line.startsWith("  "+s + " ")){
                return s;
            }
            if(line.startsWith("   "+s + " ")){
                return s;
            }
        }
        return null;
    }


    private int addList(int i, String[] lines, MdContent body, Function<String, Li> strToLi, Function<String, Boolean> match) {
        LiGroup group = new LiGroup();
        group.setLiList(new ArrayList<>());
        body.getBlogNodes().add(group);
        Li li = new Li();
        li.setContent(new ArrayList<>());
        group.getLiList().add(li);

        Li temp = strToLi.apply(lines[i]);
        li.setPrefix(temp.getPrefix());
        li.getContent().add(temp.getContent().get(0));

        i++;
        for (; i < lines.length; i++) {
            if (match.apply(lines[i])) {
                li = strToLi.apply(lines[i]);
                group.getLiList().add(li);
                continue;
            } else if (isNewLine(lines[i])) {
                return i;
            } else {
                li.getContent().add(lines[i]);
            }
        }
        return i;
    }

    private boolean isNewLine(String line) {
        if(line.trim().startsWith("#")){
            return true;
        }
        return line.trim().isEmpty();
    }


    private int addContent(int i, String[] lines, MdContent body) {
        Text text = new Text();
        text.setContent(new ArrayList<>());
        body.getBlogNodes().add(text);
        for (; i < lines.length; i++) {
            if (lines[i].startsWith(CODE)) {
                return i - 1;
            }
            if (lines[i].startsWith(TABLE_FLAG)) {
                return i - 1;
            }
            if (lines[i].startsWith(BODY_SPLIT)) {
                return i - 1;
            }
            if (lines[i].matches(NUMBER_LIST)) {
                return i - 1;
            }
            if (isList(lines[i])!=null) {
                return i - 1;
            }
            text.getContent().add(lines[i]);
        }
        return i;
    }

    private int addTable(int i, String[] lines, MdContent mdContent) {
        //如果是最后一行直接返回text
        if (lines.length == i + 1) {
            Text text = new Text();
            text.setContent(new ArrayList<>());
            text.getContent().add(lines[i]);
            mdContent.getBlogNodes().add(text);
            return i;
        }
        Table table = new Table();
        table.setHead(lines[i]);
        i++;
        table.setSplit(lines[i]);
        table.setBody(new ArrayList<>());
        mdContent.getBlogNodes().add(table);
        if (lines.length == i + 2) {
            return i;
        }
        i++;
        for (; i < lines.length; i++) {
            if (!lines[i].startsWith("|")) {
                return i - 1;
            }
            table.getBody().add(lines[i]);
        }
        return i;
    }

    private int addCode(int i, String[] lines, MdContent mdContent) {
        String type = lines[i].substring(3);
        Code code = new Code();
        code.setType(type);
        code.setContent(new ArrayList<>());
        mdContent.getBlogNodes().add(code);
        i++;
        for (; i < lines.length; i++) {
            if (lines[i].startsWith("```")) {
                return i;
            }
            code.getContent().add(lines[i]);
        }
        return i;
    }


    private void addTitle(String[] lines) {
        //忽略第一个---前面的内容
        Title title = null;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith(TITLE_FLAG)) {
                if (title == null) {
                    title = new Title();
                    title.setContent(new ArrayList<>());
                    continue;
                } else {
                    blog.setTitle(title);
                    addSummary(i + 1, lines);
                    return;
                }
            }
            if (title == null) {
                continue;
            }
            title.getContent().add(lines[i]);
        }
    }

}
