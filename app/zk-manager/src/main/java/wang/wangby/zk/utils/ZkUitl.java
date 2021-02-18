package wang.wangby.zk.utils;

import org.apache.zookeeper.*;

import java.util.List;

public class ZkUitl {
    public static void main(String args[]) throws Exception {
       int  CLIENT_PORT=2181;
        // 创建一个与服务器的连接
        ZooKeeper zk = new ZooKeeper("centos:" + CLIENT_PORT, 5000, new Watcher() {
            // 监控所有被触发的事件
            public void process(WatchedEvent event) {
                System.out.println("已经触发了" + event.getType() + "事件！");
            }
        });
        List<String> list=zk.getChildren("/",true);
        for(String s:list){
            System.out.println(s);
        }

//        // 创建一个目录节点
//        tester.create("/testRootPath", "testRootData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
//                CreateMode.PERSISTENT);
//        // 创建一个子目录节点
//        tester.create("/testRootPath/testChildPathOne", "testChildDataOne".getBytes(),
//                ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
//        System.out.println(new String(tester.getData("/testRootPath",false,null)));
//        // 取出子目录节点列表
//        System.out.println(tester.getChildren("/testRootPath",true));
//        // 修改子目录节点数据
//        tester.setData("/testRootPath/testChildPathOne","modifyChildDataOne".getBytes(),-1);
//        System.out.println("目录节点状态：["+tester.exists("/testRootPath",true)+"]");
//        // 创建另外一个子目录节点
//        tester.create("/testRootPath/testChildPathTwo", "testChildDataTwo".getBytes(),
//                ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
//        System.out.println(new String(tester.getData("/testRootPath/testChildPathTwo",true,null)));
//        // 删除子目录节点
//        tester.delete("/testRootPath/testChildPathTwo",-1);
//        tester.delete("/testRootPath/testChildPathOne",-1);
//        // 删除父目录节点
//        tester.delete("/testRootPath",-1);
//        // 关闭连接
        zk.close();
    }
}
