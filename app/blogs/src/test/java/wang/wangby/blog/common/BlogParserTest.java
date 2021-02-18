package wang.wangby.blog.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import wang.wangby.blog.model.markdown.Blog;
import wang.wangby.test.TestBase;

import java.io.File;
import java.util.List;

@Slf4j
public class BlogParserTest extends TestBase {

    @Test
    public void parseLi() {
        String file=BlogParserTest.class.getResource("/md/list.md").getFile();
        BlogParser parser = new BlogParser(new File(file));
        Blog blog=parser.getBlog();
        log.debug(toJson(blog));
    }

    @Test
    public void parseTable() {
        String file=BlogParserTest.class.getResource("/md/table.md").getFile();
        BlogParser parser = new BlogParser(new File(file));
        Blog blog=parser.getBlog();
        log.debug(toJson(blog));
    }

    @Test
    public void parse() {
        String dir=BlogParserTest.class.getResource("/md").getFile();
        List<Blog> blogs=BlogParser.getAll(dir);
        for(Blog b:blogs){
            print(b);
        }
    }

    private void print(Blog b) {
        log.debug("发现blog:"+toJson(b));
        log.debug("id={},category={},summarLength={}",b.getId(),b.getCategory(),b.getSummary().getBlogNodes().size());
    }
}