package wang.wangby.utils.tree;

public interface NodeVistor<T extends TreeNode> {
    //访问某个节点
    boolean vister(T node, int idxOrLayer);
}
