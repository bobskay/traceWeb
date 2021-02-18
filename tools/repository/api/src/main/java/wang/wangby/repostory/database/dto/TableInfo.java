package wang.wangby.repostory.database.dto;

import lombok.Data;
import wang.wangby.annotation.Remark;

import java.util.ArrayList;
import java.util.List;

@Data
@Remark("表信息")
public class TableInfo  {
	@Remark("表名")
	private String tableName;
	@Remark("表注释")
	private String tableComment;
	@Remark("对应数据信息")
	private DatabaseInfo databaseInfo;
	@Remark("包含的列")
	private List<ColumnInfo> columns;

	public String pkName(){
		for(ColumnInfo col:columns){
			if(col.getIsPk()){
				return col.getColumnName();
			}
		}
		return  null;
	}

	public List<ColumnInfo> getColumnsWithoutPk(){
		List list=new ArrayList();
		for(ColumnInfo col:columns){
			if(col.getIsPk()!=null&&col.getIsPk()){
				continue;
			}
			list.add(col);
		}
		return  list;
	}
}
