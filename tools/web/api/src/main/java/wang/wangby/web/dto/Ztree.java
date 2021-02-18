package wang.wangby.web.dto;

import lombok.Data;

import java.util.*;
import java.util.function.Function;

@Data
public class Ztree {
    private String name;
    private List<Ztree> children;
    private boolean open;
    private String parentId;
    private Integer index;
    private String id;
    private boolean checked;


    public static <T> List<Ztree> createTree(List<T> list, Function<T, Ztree> toZtree) {
        Map<String, Ztree> all = new TreeMap<>();
        //将对象转为BootstrapTreeView并放入map
        for (T t : list) {
            Ztree ztree = toZtree.apply(t);
            if (ztree.index == null) {
                ztree.index = 0;
            }
            all.put(ztree.id, ztree);
        }
        return formate(all);
    }

    //将list组装成树,并排序
    private static List<Ztree> formate(Map<String, Ztree> all) {
        List<Ztree> roots = new ArrayList();
        //按上下级组装成树,没有上级的当做跟节点
        for (Ztree view : all.values()) {
            Ztree parent = all.get(view.getParentId());
            if (parent == null || parent == view) {
                roots.add(view);
                continue;
            }
            if (parent.children == null) {
                parent.children = new ArrayList<>();
            }
            parent.children.add(view);
        }
        //排序
        Comparator<Ztree> comparator=(v1, v2)->{
            return v1.index - v2.index;
        };
        for (Ztree view : all.values()) {
            if (view.getChildren() != null) {
                Collections.sort(view.getChildren(),comparator);
            }
        }
        Collections.sort(roots,comparator);
        return roots;
    }

}
