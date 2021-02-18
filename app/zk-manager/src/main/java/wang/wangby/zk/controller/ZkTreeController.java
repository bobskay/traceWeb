package wang.wangby.zk.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.Remark;
import wang.wangby.entity.request.Response;
import wang.wangby.web.controller.BaseController;
import wang.wangby.web.dto.Ztree;
import wang.wangby.zk.model.ZkInfo;
import wang.wangby.zk.model.ZooNode;
import wang.wangby.zk.service.ZkInfoService;
import wang.wangby.zk.service.ZooKeeperService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RequestMapping("/zkTree")
@RestController
@Slf4j
public class ZkTreeController extends BaseController {


    @Autowired
    ZooKeeperService zooKeeperService;
    @Autowired
    ZkInfoService zkInfoService;

    @RequestMapping("/index")
    @Remark("zookeeper节点树")
    public String index(Long id) throws Exception {
        ZkInfo info = zkInfoService.get(id);
        return $("index", info);
    }


    @RequestMapping("/tree")
    @ResponseBody
    public Response<List<Ztree>> tree(String zkAddress) throws IOException, KeeperException, InterruptedException {
        List<ZooNode> zooNodes = zooKeeperService.getNodes(zkAddress,"/");
        List list = Ztree.createTree(zooNodes, t -> {
           return nodeToZtree(t);
        });
        return respone(list);
    }

    @RequestMapping("/get")
    public String get(String zkAddress,String path) throws KeeperException, InterruptedException, UnsupportedEncodingException {
        //path= URLEncoder.encode(path,"UTF-8");
        ZooNode node=zooKeeperService.getNode(zkAddress,path);
        return $("get",node);

    }

   @RequestMapping("/prepareInsert")
   public String prepareInsert(String zkAddress,String path) {
        ZooNode node=new ZooNode();
        node.setZkAddress(zkAddress);
        node.setParentPath(path);
       return $("prepareInsert",node);
   }

    @RequestMapping("/insert")
   public Response<Ztree> insert(ZooNode zooNode) throws KeeperException, InterruptedException {
       String path=zooKeeperService.insert(zooNode.getZkAddress(),zooNode.getParentPath(),zooNode.getName(),zooNode.getDataString());
       Ztree ztree=nodeToZtree(zooNode);
        ztree.setId(path);
       return respone(ztree);
   }

   private Ztree nodeToZtree(ZooNode node){
       Ztree view = new Ztree();
       view.setParentId(node.getParentPath());
       view.setId(node.getPath());
       view.setName(node.getName());
       return view;
   }

    @RequestMapping("/delete")
    public Response<Boolean> delete(String zkAddress,String path,Integer version) throws KeeperException, InterruptedException {
       try{
           zooKeeperService.delete(zkAddress,path,version);
           return  respone(true);
       }catch (KeeperException ex){
           if(ex.code()== KeeperException.Code.NOTEMPTY){
               return fail("必须先删除子节点");
           }
           return fail("删除失败:"+ex.getMessage());
       }
    }

    @RequestMapping("/prepareUpdate")
    public String prepareUpdate(ZooNode zooNode) throws KeeperException, InterruptedException {
        zooNode=zooKeeperService.getNode(zooNode.getZkAddress(),zooNode.getPath());
        return $("prepareUpdate",zooNode);
    }

    @RequestMapping("/update")
    public Response<Ztree> update(ZooNode zooNode){
        try{
            zooKeeperService.update(zooNode.getZkAddress(),zooNode.getPath(),zooNode.getDataString(),zooNode.getStat().getVersion());
            return Response.success(nodeToZtree(zooNode));
        }catch (KeeperException | InterruptedException ex){
            return Response.fail(ex.getMessage());
        }
    }

    @RequestMapping("/deleteBatch")
    public Response<Ztree> deleteBatch(String zkAddress,String path,Integer version){
        try{
            zooKeeperService.deleteBatch(zkAddress,path,version);
            return  respone(true);
        }catch (KeeperException | InterruptedException ex){
            return fail("删除失败:"+ex.getMessage());
        }
    }
}
