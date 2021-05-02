package wang.wangby.repostory.database.dto;

import lombok.Data;
import wang.wangby.annotation.Remark;

@Data
@Remark("数据库列信息")
public class ColumnInfo {


	@Remark("表名")
	private String tableName;
	@Remark("列名")
	private String  columnName;
	@Remark("是否允许为空")
	private Boolean nullable;
	@Remark("数据库类型")
	private String dataType;
	@Remark("允许的最大长度")
	private Integer maxLength;
	@Remark("列备注")
	private String columnComment;
	@Remark("是否是主键")
	private Boolean isPk;

	private Integer decimal;

	public String getDataTypeStr(){
		if(maxLength==null){
			return dataType;
		}
		if(decimal!=null){
			return dataType+"("+maxLength+","+decimal+")";
		}else{
			return dataType+"("+maxLength+")";
		}

	}
}
