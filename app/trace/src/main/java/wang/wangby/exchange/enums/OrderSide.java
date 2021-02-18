package wang.wangby.exchange.enums;

import lombok.Getter;

/**
 * buy, sell, both.
 */

public enum OrderSide {
  BUY("B","买"),
  SELL("S","卖");

  @Getter
  public final String code;
  @Getter
  public final String name;

  OrderSide(String side,String name) {
    this.code = side;
    this.name=name;
  }

  public static OrderSide getById(String orderId) {
    for(OrderSide os:OrderSide.values()){
      if(orderId.startsWith(os.code)){
        return os;
      }
    }
    return null;
  }
}