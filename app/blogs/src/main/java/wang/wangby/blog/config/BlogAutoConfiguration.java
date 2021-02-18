package wang.wangby.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wang.wangby.blog.common.BlogMenuProvider;
import wang.wangby.blog.common.BlogParser;
import wang.wangby.blog.common.Blogs;
import wang.wangby.blog.model.markdown.Blog;
import wang.wangby.template.TemplateUtil;
import wang.wangby.utils.FileUtil;
import wang.wangby.web.MenuProvider;

import java.io.File;
import java.util.List;

@Configuration
@Slf4j
public class BlogAutoConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "my.blog")
    public BlogProperties blogProperties() {
        return new BlogProperties();
    }

    @Bean
    //解析所有md文件,并将图片复制到工作目录下
    public Blogs blogs(BlogProperties blogProperties, TemplateUtil templateUtil) {
        List<Blog> list = BlogParser.getAll(blogProperties.getMarkdownDir());
        Blogs blogs = new Blogs(list, templateUtil);
        File imageDir = new File(blogProperties.getImgDir());
        String workdDir = this.getClass().getResource("/").getFile();
        String to = new File(workdDir + "/static/blog/images").getAbsolutePath();
        String imageDirF = FileUtil.format(imageDir.getAbsolutePath());
        FileUtil.iterator(imageDir, (dir, file) -> {
            String dirF = FileUtil.format(dir.getAbsolutePath());
            String newDir = dirF.replace(imageDirF, "");
            if (!newDir.startsWith("/")) {
                newDir += "/";
            }
            File toDir = new File(to + newDir);
            toDir.mkdirs();

            String fileF = FileUtil.format(file.getAbsolutePath());
            String newFile = fileF.replace(imageDirF, "");
            if (!newFile.startsWith("/")) {
                newFile += "/";
            }
            newFile = to + newFile;
            log.debug("复制文件:{}到{}", file.getAbsolutePath(), newFile);
            FileUtil.copy(file.getAbsolutePath(), newFile);
            return true;
        });
        return blogs;
    }

    @Bean
    public MenuProvider menuProvider(Blogs blogs) {
        return new BlogMenuProvider(blogs);
    }
}
