package wang.wangby.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EnumUtil {
	public static void setEnum(Object bean, String name, String value, Class clazz) {
		Object[] enums = null;
		try {
			enums = (Object[]) clazz.getMethod("values").invoke(clazz);
		} catch (Exception ex) {
			throw new RuntimeException("设置枚举属性出错"+ex.getMessage(),ex);
		}
		if (StringUtil.isInteger(value)) {
			BeanUtil.set(bean, name, enums[Integer.parseInt(value)]);
			return;
		}
		for (Object o : enums) {
			if (o.toString().equals(value)) {
				BeanUtil.set(bean, name, o);
				return;
			}
		}
		throw new RuntimeException("无法将" + value + "转为还枚举" + clazz);
	}

	/** 获得一个字段的所有属性 */
	public static List<String> getFileds(Class clazz) {
		Set<String> setFields = BeanUtil.getSetFields(clazz);
		List<String> fields = new ArrayList();
		for (Method method : clazz.getMethods()) {
			String methodName = method.getName();
			if (method.getParameterTypes().length != 0) {
				continue;
			}
			if (methodName.startsWith("get") && !methodName.equals("get")) {
				String name = BeanUtil.getFieldName(method.getName());
				if (setFields.contains(name)) {
					fields.add(name);
				}
			}
		}
		return fields;
	}

	// 通过枚举类获得所有枚举值
	public static Enum[] getEnums(Class type) {
		try {
			Method method = type.getMethod("values");
			return (Enum[]) method.invoke(type);
		} catch (Exception ex) {
			throw new RuntimeException("执行方法" + type + ".values()出错");
		}
	}

}
