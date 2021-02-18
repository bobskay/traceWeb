package wang.wangby.utils.tree;

import java.util.List;

public interface TreeNode {
    Integer getIndex();
    void setIndex(Integer index);

    String getId();
    void setId(String id);

    String getParentId();
    void setParentId(String parentId);

    List getChildren();
    void setChildren(List children);
}
