package wang.wangby.repostory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import wang.wangby.permission.entity.User;
import wang.wangby.test.MyTestRunner;
import wang.wangby.test.TestBase;

import java.util.List;

@RunWith(MyTestRunner.class)
public class MapRepositoryTest extends TestBase {

    @Autowired
    MapRepository mapRepository;

    @Test
    public void insert() {
        User user=new User();
        user.setUserName("hello");
        user=mapRepository.insert(user);
        log.debug(user+"");
    }

    @Test
    public void select() {
        for(int i=0;i<10;i++){
            User user=new User();
            user.setUserName("name"+i);
            mapRepository.insert(user);
        }
        List<User> list=mapRepository.select(new User(),5,2);
        log.debug("找到数据:"+ list.size());
        for(User u:list){
            log.debug(u.getUserName());
        }
    }
}