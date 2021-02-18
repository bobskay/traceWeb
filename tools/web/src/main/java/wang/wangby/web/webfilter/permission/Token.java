package wang.wangby.web.webfilter.permission;

import lombok.Data;
import wang.wangby.annotation.Remark;

@Data
public class Token {
    public static final String COOKIE_NAME="mytoken";
    public static final String HEADER_NAME="Authorization";
    
    @Remark("token标识")
    private String tokenId;
    @Remark("创建时间")
    private long createTime;
    @Remark("用户标识")
    private String userName;
    @Remark("过期时间,-1表示永不过期")
    private long expireTime;


    public static Token getToken(String tokenId) {
        Token token=new Token();
        token.setTokenId(tokenId);
        String[] tokens=tokenId.split("_");
        if(tokens.length<3){
            return null;
        }
        token.setUserName(tokens[0]);
        token.setCreateTime(Long.parseLong(tokens[1]));
        token.setExpireTime(Long.parseLong(tokens[2]));
        return token;

    }

    public static String getTokenId(String userName,long expSecond) {
        long exp=expSecond;
        if(exp!=-1){
            exp=System.currentTimeMillis()+expSecond*1000;
        }
        return userName+"_"+System.currentTimeMillis()+"_"+exp;
    }

    public boolean isExpired(){
        return System.currentTimeMillis()>expireTime;
    }
}
