package wang.wangby.exchange.enums;



/**
 * withdraw, deposit.
 */
public enum DepositState {

  UNKNOWN("unknown"),
  CONFIRMING("confirming"),
  SAFE("safe"),
  CONFIRMED("confirmed"),
  ORPHAN("orphan");


  private final String code;

  DepositState(String code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return code;
  }



}
