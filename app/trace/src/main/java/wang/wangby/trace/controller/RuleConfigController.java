package wang.wangby.trace.controller;


import cn.hutool.core.bean.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.web.Menu;

import wang.wangby.trace.config.MarketConfig;

import wang.wangby.trace.service.MarketService;

import wang.wangby.web.controller.BaseController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("ruleConfig")
public class RuleConfigController extends BaseController {

    @Autowired
    MarketService marketService;
    @Autowired
    MarketConfig marketConfig;

    @Menu("参数配置")
    @RequestMapping("index")
    public String index() {
        Map map = new HashMap<>();
        map.put("price", marketService.getPrice());
        map.put("marketConfig", marketConfig);
        return $("index", map);
    }

    @RequestMapping("updateConfig")
    public String updateConfig(MarketConfig marketConfig){
        BeanUtil.copyProperties(marketConfig,this.marketConfig);
        return jsonUtil.toFormatString(this.marketConfig);
    }
}
