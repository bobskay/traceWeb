package wang.wangby.test;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringTestBase.class})// 指定启动类
@Slf4j
public class SpringTestBase {

    @Test
    public void go(){

    }
}
