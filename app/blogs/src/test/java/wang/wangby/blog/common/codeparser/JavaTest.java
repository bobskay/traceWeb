package wang.wangby.blog.common.codeparser;

import org.junit.Test;
import wang.wangby.blog.common.codeparser.vo.StringItem;
import wang.wangby.test.TestBase;
import wang.wangby.test.assertutils.MyAssert;

import java.util.ArrayList;
import java.util.List;

public class JavaTest extends TestBase {

    @Test
    public void toStringItem() {
        String text="aaa'\\\"'bbb+\"\\'abc\\\"xxx  \""+" abc";
        log.debug(text);
        List<StringItem> items=new ArrayList<>();
        String remain=new JavaParse(null).toStringItem(text,items);
        MyAssert.equalTo(remain," abc");
        log.debug(toJson(items));
    }

    @Test
    public void formatLine() {
        format("@able");
        format("  public String hello{");
        format("aaa  bbb  //this is a comment");
        format("if(true){");

    }

    private void format(String text){
        JavaParse java=new JavaParse(null);
        log.debug(text+" =|"+java.formatLine(text));
    }
}