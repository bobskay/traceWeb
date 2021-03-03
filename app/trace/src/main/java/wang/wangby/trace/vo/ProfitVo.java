package wang.wangby.trace.vo;

import lombok.Data;
import wang.wangby.trace.model.Profit;
import wang.wangby.utils.DateTime;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class ProfitVo {


    private List<String> times;
    private LinkedHashMap<String,ProfitDetailVo> data;

    public ProfitVo(){
        this.times=new ArrayList<>();
        data=new LinkedHashMap<>();
        data.put("价格",ProfitDetailVo.newInstance("价格"));
        data.put("账户余额",ProfitDetailVo.newInstance("账户余额"));
        data.put("交易次数",ProfitDetailVo.newInstance("交易次数"));
        data.put("交易金额",ProfitDetailVo.newInstance("交易金额"));
        data.put("交易数量",ProfitDetailVo.newInstance("交易数量"));

    }


    public List<String> getNames(){
        return new ArrayList<>(data.keySet());
    }

    public List<ProfitDetailVo> getData(){
        return new ArrayList<>(data.values());
    }


    public void addProfit(Profit profit){
        times.add(new DateTime(profit.getDate()).toString(DateTime.Format.DAY_TO_MINUTE));
        data.get("价格").getData().add(profit.getPrice().toString());
        data.get("账户余额").getData().add(profit.getAccount().toString());
        data.get("交易金额").getData().add(profit.getProfitAmount().toString());
        data.get("交易次数").getData().add(profit.getExchangeCount()+"");
        data.get("交易数量").getData().add(profit.getExchangeQuantity()+"");
    }
}
