package wang.wangby.trace.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//    {
//        name: '搜索引擎',
//                type: 'line',
//            stack: '总量',
//            data: [820, 932, 901, 934, 1290, 1330, 1320]
//    }
@Data
public class ProfitDetailVo {
    private String name;
    private String type="line";
    private String stack="";
    private List<String> data;

    public static ProfitDetailVo newInstance(String name) {
        ProfitDetailVo vo=new ProfitDetailVo();
        vo.setName(name);
        vo.setData(new ArrayList<>());
        return vo;
    }
}


