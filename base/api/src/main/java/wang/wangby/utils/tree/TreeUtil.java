package wang.wangby.utils.tree;

import lombok.extern.slf4j.Slf4j;
import wang.wangby.utils.CollectionUtil;

import java.util.*;
import java.util.function.Function;

@Slf4j
public class TreeUtil {
    public static String ROOT_ID="0";

    /**
     * 将list组装成树形结构
     * @param  list 实际数据
     * @param convertor 将目标对象转为TreeNode的方法
     * */
    public static <Source> List createTree(List<Source> list, Function<Source, TreeNode> convertor) {
        Map<String, TreeNode> nodes = new TreeMap<>();
        //将对象转为BootstrapTreeView并放入map
        for (Source source : list) {
            TreeNode node = convertor.apply(source);
            node.setChildren(new ArrayList());
            if (node.getIndex() == null) {
                node.setIndex(0);
            }
            nodes.put(node.getId(), node);
        }
        log.debug("准备组装树,节点个数size={},keys={}",nodes.size(),nodes.keySet());
        return toTree(nodes);
    }

    /**
     * 将list按层级组装成树
     * */
    public static  List toTree(List<TreeNode> nodes) {
        Map map= CollectionUtil.toMap(nodes, TreeNode::getId);
        if(nodes.size()!=map.size()){
            log.debug("转为map后节点个数减少{}-{}={}", nodes.size(),map.size(), nodes.size()-map.size());
        }
        return toTree(map);
    }

    private static  List toTree(Map<String, TreeNode> nodeMap) {
        List<TreeNode> roots = new ArrayList();
        //按上下级组装成树,没有上级的当做跟节点
        for (TreeNode node : nodeMap.values()) {
            if(node.getParentId()==null){
                log.info("节点未设置parentId,默认放到跟节点:"+node);
                node.setParentId(ROOT_ID);
            }
            //如果没有上级或者当前节点的父节点和节点一样,就放到跟节点
            TreeNode parent = nodeMap.get(node.getParentId());
            if (parent == null || parent == node) {
                roots.add(node);
                continue;
            }
            List children=parent.getChildren();
            if ( children== null) {
                children=new ArrayList();
            }
            children.add(node);
            parent.setChildren(children);
        }
        //排序
        Comparator<TreeNode> comparator= Comparator.comparingInt(TreeNode::getIndex);
        for (TreeNode node : nodeMap.values()) {
            List list=node.getChildren();
            if (list != null) {
                Collections.sort(list,comparator);
            }
        }
        Collections.sort(roots,comparator);
        return roots;
    }


    //遍历
    public static  <T extends TreeNode>  void iteratorLayer(List<T> rootList, NodeVistor<T> vistor) {
        iteratorLayer(rootList,vistor,0);
    }

    //遍历
    public static  <T extends TreeNode>  void iteratorIndex(List<T> rootList, NodeVistor<T> vistor) {
        for(int i=0;i<rootList.size();i++){
            if(!vistor.vister(rootList.get(i),i)){
                return;
            }
            List<T> children=rootList.get(i).getChildren();
            if(children!=null){
                iteratorIndex(children,vistor);
            }
        }
    }


    public static void iteratorLayer(List<? extends TreeNode> list, NodeVistor vistor, int layer) {
        for(TreeNode node:list){
            if(!vistor.vister(node,layer)){
                return;
            }
            List<TreeNode> children=node.getChildren();
            if(children!=null){
                iteratorLayer(children,vistor,layer+1);
            }
        }
    }
}
