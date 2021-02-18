package wang.wangby.tools.controller.vo;

import lombok.Data;
import wang.wangby.tools.monitor.dto.HitInfo;

import java.util.List;

@Data
public class CacheDetail {

    HitInfo total;
    private String name;
    private List<String> keys;
}
