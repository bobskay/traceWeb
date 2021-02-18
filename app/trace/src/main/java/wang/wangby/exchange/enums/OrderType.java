package wang.wangby.exchange.enums;


/**
 * LIMIT 限价单
 * MARKET 市价单
 * STOP 止损限价单
 * STOP_MARKET 止损市价单
 * TAKE_PROFIT 止盈限价单
 * TAKE_PROFIT_MARKET 止盈市价单
 * TRAILING_STOP_MARKET 跟踪止损单
 */
public enum OrderType {
    LIMIT("LIMIT"),
    MARKET("MARKET"),
    STOP("STOP"),
    STOP_MARKET("STOP_MARKET"),
    TAKE_RPOFIT("TAKE_RPOFIT"),
    TAKE_RPOFIT_MARKET("TAKE_RPOFIT_MARKET"),
    INVALID(null);

  private final String code;

  OrderType(String code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return code;
  }

  private static final EnumLookup<OrderType> lookup = new EnumLookup<>(OrderType.class);

  public static OrderType lookup(String name) {
    return lookup.lookup(name);
  }

}
