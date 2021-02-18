package wang.wangby.zk.model;

import lombok.Data;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;

@Data
public class ZooNode {

    private String path;
    private String parentPath;
    private String name;
    private String dataString;
    private Stat stat;
    private List<ACL> acls;
    private String zkAddress;

    public String getPermsText(ACL acl){
        StringBuilder sb=new StringBuilder();
        if((acl.getPerms()& ZooDefs.Perms.CREATE )!=0){
            sb.append("CREATE,");
        }
        if((acl.getPerms()& ZooDefs.Perms.DELETE )!=0){
            sb.append("DELETE,");
        }
        if((acl.getPerms()& ZooDefs.Perms.READ )!=0){
            sb.append("READ,");
        }
        if((acl.getPerms()& ZooDefs.Perms.WRITE )!=0){
            sb.append("WRITE,");
        }
        if((acl.getPerms()& ZooDefs.Perms.ADMIN )!=0){
            sb.append("ADMIN,");
        }
        if(sb.length()>0){
            return sb.substring(0,sb.length()-1);
        }
        return "none";
    }
}
