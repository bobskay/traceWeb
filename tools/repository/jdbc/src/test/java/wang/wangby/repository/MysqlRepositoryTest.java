package wang.wangby.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import wang.wangby.permission.entity.User;
import wang.wangby.repostory.EntityDaoFinder;
import wang.wangby.test.MyTestRunner;
import wang.wangby.test.TestBase;

@RunWith(MyTestRunner.class)
public class MysqlRepositoryTest extends TestBase {

    @Autowired
    MysqlRepository mysqlRepository;

    @Test
    public void insert() {
//        EntityDaoFinder finder=new EntityDaoFinder(u->{
//           return null;
//        });
//        addBean(EntityDaoFinder.class,finder);
//
//        User user=new User();
//        user.setUserName("hello");
//        mysqlRepository.insert(user);
//        log.debug("新增成功:"+user);
    }


}