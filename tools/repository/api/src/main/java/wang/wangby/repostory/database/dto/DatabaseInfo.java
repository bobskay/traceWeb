package wang.wangby.repostory.database.dto;

import lombok.Data;
import wang.wangby.annotation.Remark;

@Data
@Remark("数据库信息")
public class DatabaseInfo {

	@Remark("用户名")
	private String username;
	@Remark("密码")
	private String password;
	@Remark("连接数据库字符串")
	private String url;
	@Remark("数据库类型")
	private String dbType="mysql";
	@Remark
	private String alias;

	public String getKey() {
		return username+"|"+ password.replaceAll(".","*")+"|"+url;
	}
	
}
