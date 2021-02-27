package wang.wangby.exchange.dto;

import lombok.Data;
import wang.wangby.exchange.response.ApiResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * {
 *     "feeTier": 0,  // 手续费等级
 *     "canTrade": true,  // 是否可以交易
 *     "canDeposit": true,  // 是否可以入金
 *     "canWithdraw": true, // 是否可以出金
 *     "updateTime": 0,
 *     "totalInitialMargin": "0.00000000",  // 但前所需起始保证金总额(存在逐仓请忽略)
 *     "totalMaintMargin": "0.00000000",  // 维持保证金总额
 *     "totalWalletBalance": "23.72469206",   // 账户总余额
 *     "totalUnrealizedProfit": "0.00000000",  // 持仓未实现盈亏总额
 *     "totalMarginBalance": "23.72469206",  // 保证金总余额
 *     "totalPositionInitialMargin": "0.00000000",  // 持仓所需起始保证金(基于最新标记价格)
 *     "totalOpenOrderInitialMargin": "0.00000000",  // 当前挂单所需起始保证金(基于最新标记价格)
 *     "totalCrossWalletBalance": "23.72469206",  // 全仓账户余额
 *     "totalCrossUnPnl": "0.00000000",    // 全仓持仓未实现盈亏总额
 *     "availableBalance": "23.72469206",       // 可用余额
 *     "maxWithdrawAmount": "23.72469206"     // 最大可转出余额
 *     "assets": [
 *         {
 *             "asset": "USDT",        //资产
 *             "walletBalance": "23.72469206",  //余额
 *             "unrealizedProfit": "0.00000000",  // 未实现盈亏
 *             "marginBalance": "23.72469206",  // 保证金余额
 *             "maintMargin": "0.00000000",    // 维持保证金
 *             "initialMargin": "0.00000000",  // 当前所需起始保证金
 *             "positionInitialMargin": "0.00000000",  // 持仓所需起始保证金(基于最新标记价格)
 *             "openOrderInitialMargin": "0.00000000", // 当前挂单所需起始保证金(基于最新标记价格)
 *             "crossWalletBalance": "23.72469206",  //全仓账户余额
 *             "crossUnPnl": "0.00000000" // 全仓持仓未实现盈亏
 *             "availableBalance": "23.72469206",       // 可用余额
 *             "maxWithdrawAmount": "23.72469206"     // 最大可转出余额
 *         }
 *     ],
 *     "positions": [  // 头寸，将返回所有市场symbol。
 *         //根据用户持仓模式展示持仓方向，即双向模式下只返回BOTH持仓情况，单向模式下只返回 LONG 和 SHORT 持仓情况
 *         {
 *             "symbol": "BTCUSDT",  // 交易对
 *             "initialMargin": "0",   // 当前所需起始保证金(基于最新标记价格)
 *             "maintMargin": "0", //维持保证金
 *             "unrealizedProfit": "0.00000000",  // 持仓未实现盈亏
 *             "positionInitialMargin": "0",  // 持仓所需起始保证金(基于最新标记价格)
 *             "openOrderInitialMargin": "0",  // 当前挂单所需起始保证金(基于最新标记价格)
 *             "leverage": "100",  // 杠杆倍率
 *             "isolated": true,  // 是否是逐仓模式
 *             "entryPrice": "0.00000",  // 持仓成本价
 *             "maxNotional": "250000",  // 当前杠杆下用户可用的最大名义价值
 *             "positionSide": "BOTH",  // 持仓方向
 *             "positionAmt": "0"      // 持仓数量
 *         }
 *     ]
 * }
 * */
@Data
public class Account extends ApiResponse {

    private Integer feeTier;
    private Boolean canTrade;
    private Boolean canDeposit;
    private Boolean canWithdraw;
    private Integer updateTime;
    private String totalInitialMargin;
    private String totalMaintMargin;
    private String totalWalletBalance;
    private String totalUnrealizedProfit;
    private BigDecimal totalMarginBalance;
    private String totalPositionInitialMargin;
    private String totalOpenOrderInitialMargin;
    private String totalCrossWalletBalance;
    private String totalCrossUnPnl;
    private String availableBalance;
    private String maxWithdrawAmount;
    private List<AssetsDTO> assets;
    private List<PositionsDTO> positions;

    @Data
    public static class AssetsDTO {
        private String asset;
        private String walletBalance;
        private String unrealizedProfit;
        private String marginBalance;
        private String maintMargin;
        private String initialMargin;
        private String positionInitialMargin;
        private String openOrderInitialMargin;
        private String maxWithdrawAmount;
        private String crossWalletBalance;
        private String crossUnPnl;
        private String availableBalance;
    }

    @Data
    public static class PositionsDTO {
        private String symbol;
        private String initialMargin;
        private String maintMargin;
        private String unrealizedProfit;
        private String positionInitialMargin;
        private String openOrderInitialMargin;
        private String leverage;
        private Boolean isolated;
        private String entryPrice;
        private String maxNotional;
        private String positionSide;
        private BigDecimal positionAmt;
        private String notional;
    }
}
