package wang.wangby.codebuilder;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import wang.wangby.codebuilder.controller.vo.CodeConfig;
import wang.wangby.codebuilder.service.CodeBuilderService;
import wang.wangby.test.MyTestRunner;
import wang.wangby.test.TestBase;

@Slf4j
@RunWith(MyTestRunner.class)
public class AppBuilder extends TestBase {

    @Autowired
    CodeBuilderService codeBuilderService;

    @Test
    public void create() {
        createCode("hello","测试","/opt/temp");
    }

    private void createCode(String name, String title,String out) {

        String template=AppBuilder.class.getResource("/codeTemplate/empty").getFile();
        CodeConfig config = new CodeConfig();
        config.setAppName(name);
        config.setMenuName(title);
        config.setModelName(name);
        codeBuilderService.createCode(config, template, out);
    }

    private CodeConfig getCodeConfig() {
        return new CodeConfig();
    }
}
