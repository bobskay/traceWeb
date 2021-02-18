package wang.wangby.utils;

//Lambda表达式使用的变量只能是final的,所以不能使用外部的i++
public class Integers {
    private int i;
    public Integers(int i){
        this.i=i;
    }

    public int incrementAndGet(){
        i++;
        return i;
    }

    public int get(){
        return i;
    }
}
