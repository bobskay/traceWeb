package wang.wangby.trace.enums;

public enum MyOrderStatus {

   create(1),active(2),finish(3);

    public final int code;
    MyOrderStatus(int code){
        this.code=code;
    }

}
