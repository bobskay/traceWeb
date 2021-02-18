package wang.wangby.zk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.wangby.Constants;
import wang.wangby.exception.Message;
import wang.wangby.repostory.Repository;
import wang.wangby.utils.IdWorker;
import wang.wangby.zk.model.ZkInfo;

import java.util.List;

@Service
public class ZkInfoService {

    @Autowired
    Repository fileRepository;

    public void insert(ZkInfo zkInfo) throws Exception {
        for(ZkInfo info:this.getAll()){
            if(info.getAddress().equalsIgnoreCase(zkInfo.getAddress())){
                throw  new Message("配置已经存在:"+zkInfo.getAddress());
            }
        }
        zkInfo.setId(IdWorker.nextLong());
        fileRepository.insert(zkInfo);
    }

    public List<ZkInfo> getAll() throws Exception {
       return fileRepository.select(new ZkInfo(),0, Constants.MAX_PER_QUERY);
    }

    public ZkInfo get(Long id) throws Exception {
        return  fileRepository.get(ZkInfo.class,id);
    }

    public void delete(long id) throws Exception {
        fileRepository.delete(ZkInfo.class,id);
    }
}
