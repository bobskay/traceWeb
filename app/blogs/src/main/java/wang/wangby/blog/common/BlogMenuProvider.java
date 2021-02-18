package wang.wangby.blog.common;

import wang.wangby.blog.model.markdown.Blog;
import wang.wangby.utils.StringUtil;
import wang.wangby.web.dto.MenuInfo;
import wang.wangby.web.utils.DefaultMenuProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogMenuProvider extends DefaultMenuProvider {
    private Blogs blogs;
    Map<String, MenuInfo> dirs = new HashMap<>();

    public BlogMenuProvider(Blogs blogs) {
        this.blogs = blogs;
    }

    public List<MenuInfo> blogToMenu() {
        List<MenuInfo> menuInfos = new ArrayList<>();
        MenuInfo root=new MenuInfo();
        root.setId("/");
        dirs.put("/",root);

        for (Blog blog : blogs.getBlogMap().values()) {
            if(StringUtil.isEmpty(blog.getCategory())){
                blog.setCategory("/");
            }
            MenuInfo m = new MenuInfo();
            MenuInfo dir = dirs.get(blog.getCategory());
            if (dir == null) {
                dir = addDir(blog.getCategory(), menuInfos);
            }
            m.setText(blog.getTitle().getTitle());
            m.setUrl("/blog/get?id=" + blog.getId());
            m.setId(m.getUrl());
            m.setParentId(dir.getId());
            m.setIndex(menuInfos.size()+1);
            m.setIcon("fa fa-circle-o");
            menuInfos.add(m);
        }
        return menuInfos;
    }

    private MenuInfo addDir(String category, List<MenuInfo> menuInfos) {
        MenuInfo dir = dirs.get(category);
        if (dir != null) {
            return dir;
        }
        dir = new MenuInfo();
        dir.setId(category);
        String parentId = StringUtil.getLastBefore(category, "/");
        dir.setIndex(dirs.size());
        dir.setParentId(parentId);
        dir.setText(StringUtil.getLastAfter(category, "/"));
        dir.setIcon("fa fa-folder");
        menuInfos.add(dir);
        dirs.put(dir.getId(),dir);
        if (StringUtil.isEmpty(parentId)) {
            return dir;
        }
        MenuInfo parent = dirs.get(parentId);
        if (parent == null) {
            addDir(parentId, menuInfos);
        }
        return dir;
    }


    public List<MenuInfo> createMenu() {
        List<MenuInfo> menus = blogToMenu();
        MenuInfo search = new MenuInfo();
        search.setUrl("/blog/search");
        search.setText("全文检索");
        search.setId(search.getUrl());
        search.setParentId("/");
        search.setIndex(0);
        menus.add(0, search);
        return menus;
    }
}
