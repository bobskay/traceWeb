package wang.wangby.utils;

import java.util.ArrayList;
import java.util.List;

//固定大小的list,只能顺序压入,如果超过大小会将尾部的移除
public class FixSizeList<E> {
    private int size;
    private List<E> list;

    public FixSizeList(int size) {
        this.size = size;
        list = new ArrayList<>();
    }

    public void add(E obj) {
        synchronized (this) {
            if (list.size() >= size) {
                list.remove(list.size() - 1);
            }
            list.add(0, obj);
        }
    }

    public List<E> getData() {
        synchronized (this) {
            return new ArrayList(list);
        }
    }
}
