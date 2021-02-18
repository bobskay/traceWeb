package wang.wangby.blog.common.codeparser;

import org.junit.Test;
import wang.wangby.test.TestBase;

public class XmlParserTest extends TestBase {

    @Test
    public void addWord() {
        XmlParser p=new XmlParser(null);
        log.debug(p.formatLine("<aaa>abcd</aaa>"));
    }
}