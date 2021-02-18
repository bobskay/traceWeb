package wang.wangby;

import java.nio.charset.StandardCharsets;

public class Constants {
    public static final String LINE="\n";
    //单次最多查询多少返回多少条数据
    public static final int MAX_PER_QUERY=100000;
    public static final String OFFSET="offset";
    public static final String LIMIT="limit";
    //管理员的角色代码
    public static final String ADMIN="admin";
    public static final String UTF8 = StandardCharsets.UTF_8.displayName();
}
