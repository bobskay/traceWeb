package wang.wangby.exchange.enums;

/**
 * GTC - Good Till Cancel 成交为止
 * IOC - Immediate or Cancel 无法立即成交(吃单)的部分就撤销
 * FOK - Fill or Kill 无法全部立即成交就撤销
 * GTX - Good Till Crossing 无法成为挂单方就撤销
 * */
public enum  TimeInForce {
    GTC,
    IOC,
    FOK
}
