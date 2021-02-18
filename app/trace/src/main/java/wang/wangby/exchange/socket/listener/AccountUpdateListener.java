package wang.wangby.exchange.socket.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import wang.wangby.exchange.vo.BalanceVo;
import wang.wangby.utils.DateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {
 * "e": "ACCOUNT_UPDATE",                // 事件类型
 * "E": 1564745798939,                   // 事件时间
 * "T": 1564745798938 ,                  // 撮合时间
 * "a":                                  // 账户更新事件
 * {
 * "m":"ORDER",                      // 事件推出原因
 * "B":[                             // 余额信息
 * {
 * "a":"USDT",                   // 资产名称
 * "wb":"122624.12345678",       // 钱包余额
 * "cw":"100.12345678"           // 除去逐仓仓位保证金的钱包余额
 * },
 * {
 * "a":"BNB",
 * "wb":"1.00000000",
 * "cw":"0.00000000"
 * }
 * ],
 * "P":[
 * {
 * "s":"BTCUSDT",            // 交易对
 * "pa":"0",                 // 仓位
 * "ep":"0.00000",            // 入仓价格
 * "cr":"200",               // (费前)累计实现损益
 * "up":"0",                     // 持仓未实现盈亏
 * "mt":"isolated",              // 保证金模式
 * "iw":"0.00000000",            // 若为逐仓，仓位保证金
 * "ps":"BOTH"                   // 持仓方向
 * }，
 * <p>
 * }
 */
@Slf4j
public class AccountUpdateListener implements AccountListener {
    private Map<String, BalanceVo> balanceVoMap = new ConcurrentHashMap<>();

    @Override
    public boolean listen(JSONObject jsonObject) {
        JSONObject account = jsonObject.getJSONObject("a");
        DateTime update = new DateTime(jsonObject.getLong("T"), DateTime.Format.YEAR_TO_MILLISECOND);
        for (Object balance : account.getJSONArray("B")) {
            JSONObject ban = (JSONObject) balance;
            String symbol = ban.getString("a");// 资产名称
            symbol = symbol.toLowerCase();


            BigDecimal wb = ban.getBigDecimal("wb"); // 钱包余额
            BigDecimal cw = ban.getBigDecimal("cw");  // 除去逐仓仓
            BalanceVo newB = new BalanceVo();
            newB.setUpdated(update);
            newB.setAvailable(cw);
            newB.setTotal(wb);
            newB.setSymbol(symbol);

            BalanceVo old = balanceVoMap.get(symbol);
            if (old != null) {
                if (old.getTotal().compareTo(newB.getTotal()) != 0) {
                    BigDecimal oldS = old.getTotal().setScale(3, RoundingMode.HALF_DOWN);
                    BigDecimal newS = newB.getTotal().setScale(3, RoundingMode.HALF_DOWN);
                    log.info("更新账户余额：" + oldS + "-->" + newS + " |" + old.getTotal() + "-->" + newB.getTotal());
                }
            } else {
                log.info("初始化账户信息：" + newB);
            }
            balanceVoMap.put(symbol, newB);
        }
        return true;
    }

    @Override
    public String eventName() {
        return "ACCOUNT_UPDATE";
    }

    public BalanceVo getBalance(String symbol) {
        return balanceVoMap.get(symbol.toLowerCase());
    }

}
