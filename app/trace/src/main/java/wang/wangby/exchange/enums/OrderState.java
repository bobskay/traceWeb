package wang.wangby.exchange.enums;


/**
 NEW 新建订单
 PARTIALLY_FILLED 部分成交
 FILLED 全部成交
 CANCELED 已撤销
 REJECTED 订单被拒绝
 EXPIRED 订单过期(根据timeInForce参数规则)
 */
public enum OrderState {
  SUBMITTED,
  CREATED,
  PARTIALLY_FILLED,
  CANCELLING,
  FILLED,
  NEW,
  CANCELED;
}
