package wang.wangby.web.config;

import org.springframework.core.convert.converter.Converter;
import wang.wangby.utils.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringToDateConverter implements Converter<String, Date> {
	private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
	private static final String shortDateFormat = "yyyy-MM-dd";

	@Override
	public Date convert(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		value = value.trim();
		try {
			return convertInternal(value);
		} catch (Exception ex) {
			throw new RuntimeException("将字符串转为日期失败:" + value + "," + ex.getMessage());
		}
	}

	private Date convertInternal(String value) throws ParseException {
		if (value.matches("^\\d+$")) {
			return new Date(Long.parseLong(value));
		}
		if (value.contains(":")) {
			return new SimpleDateFormat(dateFormat).parse(value);
		} else {
			return new SimpleDateFormat(shortDateFormat).parse(value);
		}
	}

}