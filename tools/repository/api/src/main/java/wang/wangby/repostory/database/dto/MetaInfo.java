package wang.wangby.repostory.database.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

public class MetaInfo {

	private int type;
	private String name;

	//根据数据类型获取对应的java对象
	public Object getValue(ResultSet rs, int i) throws SQLException {
		switch (type) {
		case Types.VARCHAR:
			return rs.getString(i);
		case Types.BIGINT:
			return rs.getLong(i);
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			return rs.getInt(i);
		case Types.TIME:
		case Types.DATE:
			return new Date(rs.getDate(i).getTime());
		default:
			return rs.getObject(i);
		}
	}

	public String getName() {
		return name;
	}

	public void setType(int columnType) {
		this.type = columnType;
	}

	public void setName(String columnName) {
		this.name = columnName;
	}

}
