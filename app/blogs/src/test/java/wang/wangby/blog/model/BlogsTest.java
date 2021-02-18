package wang.wangby.blog.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import wang.wangby.blog.common.BlogParser;
import wang.wangby.blog.common.BlogParserTest;
import wang.wangby.blog.common.Blogs;
import wang.wangby.blog.model.markdown.Blog;
import wang.wangby.template.TemplateUtil;
import wang.wangby.test.TestBase;

import java.io.File;
import java.util.List;

@Slf4j
public class BlogsTest extends TestBase {
    @Test
    public void table(){
        String dir= BlogParserTest.class.getResource("/md").getFile();
        List<Blog> blogs=BlogParser.getAll(dir);
        Blogs bs=new Blogs(blogs, new TemplateUtil());

        String file=BlogParserTest.class.getResource("/md/test.md").getFile();
        BlogParser parser = new BlogParser(new File(file));
        Blog blog=parser.getBlog();
        String html=bs.toHtml(blog);
        log.debug("----------html-------------\n"+html);
    }

    @Test
    public void toHtml() {
        String dir= BlogParserTest.class.getResource("/md").getFile();
        List<Blog> blogs=BlogParser.getAll(dir);
        Blogs bs=new Blogs(blogs,new TemplateUtil());
        Blog bg=bs.getBlogMap().values().iterator().next();
        //log.debug("-----blogs-----------\n"+toJson(bg));
        String html=bs.toHtml(bg);
        log.debug("----------html-------------\n"+html);
    }
}