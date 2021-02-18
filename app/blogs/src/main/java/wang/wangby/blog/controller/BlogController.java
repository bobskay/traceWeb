package wang.wangby.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.blog.common.Blogs;
import wang.wangby.blog.controller.vo.BlogSearch;
import wang.wangby.blog.model.markdown.*;
import wang.wangby.entity.request.Response;
import wang.wangby.utils.StrBuilder;
import wang.wangby.web.controller.BaseController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/blog")
public class BlogController extends BaseController {

    @Autowired
    Blogs blogs;

    private volatile Response<List<BlogSearch>> blogSearches;


    @RequestMapping("get")
    public String get(String id) {
        Blog blog = blogs.getBlogMap().get(id);
        Map map = new HashMap<>();
        map.put("blog", blog);
        map.put("blogHtml", blogs.toHtml(blog));
        return $("get", map);
    }


    @RequestMapping("search")
    public String search() {
        return $("search");
    }

    @RequestMapping("blogjs")
    public Response<List<BlogSearch>> blogjs() {
        if (blogSearches != null) {
            return blogSearches;
        }
        synchronized (this) {
            if (blogSearches != null) {
                return blogSearches;
            }
        }

        List<BlogSearch> list = new ArrayList();
        blogs.getBlogMap().values().forEach(blog -> {
            BlogSearch s = new BlogSearch();
            s.setTitle(blog.getTitle().getTitle());
            s.setContent(toSearchContent(blog));
            s.setUrl("/blog/get?id=" + blog.getId());
            list.add(s);
        });
        blogSearches = respone(list);
        return blogSearches;
    }

    private String toSearchContent(Blog blog) {
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
                for (String text : ((Text) node).getContent()) {
                    sb.append(text);
                }
                continue;
            }
            if (node instanceof Code) {
                for (String text : ((Code) node).getContent()) {
                    sb.append(text);
                }
                continue;
            }
            if (node instanceof Table) {
                for (String text : ((Table) node).getBody()) {
                    sb.append(text);
                }
                continue;
            }
            for (Li li : ((LiGroup) node).getLiList()) {
                for (String s : li.getContent()) {
                    sb.append(s);
                }
            }
        }
    }

}
