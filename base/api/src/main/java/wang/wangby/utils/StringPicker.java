package wang.wangby.utils;


//字符串提取工具类
public class StringPicker {

    private StringBuilder sb;

    public StringPicker(String str) {
        sb = new StringBuilder(str);
    }

    public StringPicker(StringBuilder stringBuilder) {
        sb =stringBuilder;
    }

    //截取包含指定字符的字符串
    public String next(char[] chars) {
        char[] data = sb.toString().toCharArray();
        StringBuilder foundStr = new StringBuilder();
        int found = 0;
        for (int i = 0; i < data.length; i++) {
            if (!contain(chars, data[i])) {
                break;
            }
            foundStr.append(data[i]);
            found++;
        }
        sb.delete(0, found);
        return foundStr.toString();
    }

    public String substring(int length){
        if(sb.length()<length){
            return sb.toString();
        }
        return sb.substring(0,length);
    }

    /**
     * 截取一系列字符之前的字符串
     *
     * @param chars    可以包含的字符
     * @param splitOut 找到的分隔符
     * @return 找到的字符串
     */
    public String next(char[] chars, StringBuilder splitOut) {
        char[] data = sb.toString().toCharArray();
        StringBuilder foundStr = new StringBuilder();
        int idx = 0;
        boolean found=false;
        for (; idx < data.length; idx++) {
            if (!contain(chars, data[idx])) {
                //第一不匹配加入返回字符串里
                if (!found) {
                    foundStr.append(data[idx]);
                    continue;
                }
                break;
            }
            found = true;
            splitOut.append(data[idx]);
        }
        if(idx!=data.length){
            sb.delete(0, idx);
        }else{
            sb=new StringBuilder();
        }
        return foundStr.toString();
    }

    private boolean contain(char[] chars, char ch) {
        for (int i = 0; i < chars.length; i++) {
            if (ch == chars[i]) {
                return true;
            }
        }
        return false;
    }

    //第一个字符串是否早于第二个字符串
    public boolean isBefore(String first, String second){
        int a=sb.indexOf(first);
        int b=sb.indexOf(second);
        return a<b;

    }

     //获取指定字符串开始和结束的字节
    public String next(String begin, String end, boolean includeSplit) {
        if (sb.indexOf(begin) == -1) {
            throw new RuntimeException(toString(1000) + "不包含:" + begin);
        }
        next(begin);
        if (sb.indexOf(end) == -1) {
            throw new RuntimeException(toString(1000) + "不包含:" + end);
        }
        if (includeSplit) {
            return begin + next(end) + end;
        } else {
            return next(end);
        }
    }

    public String toString(int max) {
        if (sb.length() > max + 3) {
            return sb.substring(0, max - 3) + "...";
        }
        return sb.toString();
    }

    public String next(int length) {
        if (sb.length() < length) {
            return nextAll();
        }
        String str = sb.substring(0, length);
        sb.delete(0, length);
        return str;
    }

    public int indexOf(String str){
        return sb.indexOf(str);
    }

    public String next(String separator) {
        int i = sb.indexOf(separator);
        if (i == -1) {
            return null;
        }
        String str = sb.substring(0, i);
        sb.delete(0, i + separator.length());
        return str;
    }

    //往头部添加字符串,当next获取的数据不对是,将得到的数据还原,以便通过其它分隔符获取
    public void push(String str){
        sb.insert(0,str);
    }

    public String nextAll() {
        String str = sb.toString();
        sb = new StringBuilder();
        return str;
    }

    public String remain(){
        return sb.toString();
    }

    public boolean isEmpty(){
        return sb.length()==0;
    }

}
