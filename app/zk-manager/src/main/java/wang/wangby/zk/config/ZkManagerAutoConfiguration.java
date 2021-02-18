package wang.wangby.zk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wang.wangby.web.MenuProvider;
import wang.wangby.web.dto.MenuInfo;
import wang.wangby.web.utils.DefaultMenuProvider;
import wang.wangby.zk.model.ZkInfo;
import wang.wangby.zk.service.ZkInfoService;

import java.util.List;

@Configuration
public class ZkManagerAutoConfiguration {

    @Bean
    public MenuProvider menuProvider(ZkInfoService zkInfoService) {
        return new DefaultMenuProvider(){
            public List<MenuInfo> createMenu() {
                List<MenuInfo> menus=super.createMenu();
                try {
                    for(ZkInfo zkInfo:zkInfoService.getAll()){
                        MenuInfo m=new MenuInfo();
                        m.setText(zkInfo.getName());
                        m.setId("/zkTree/index?id="+zkInfo.getId());
                        m.setUrl(m.getId());
                        menus.add(m);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("获取zk信息出错",e);
                }
                return menus;
            }
        };
    }
}
