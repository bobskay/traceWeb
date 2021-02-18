package wang.wangby.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author ming
 */
public class BeanUtil {
	public static Logger log = LoggerFactory.getLogger(BeanUtil.class);

	public static Object get(Object o, String field) {
		if (o == null) {
			throw new RuntimeException("调用get时传入的对象为null");
		}

		if (o instanceof Map) {
			return ((Map) o).get(field);
		}

		String get = "get" + StringUtil.firstUp(field);
		try {
			Method method = o.getClass().getMethod(get);
			return method.invoke(o);
		} catch (Exception ex) {
			throw new RuntimeException("调用get出错:" + o.getClass().getName() + "." + field, ex);
		}
	}

	/**
	 * 调用set方法设置字段值 具体实现为,获取第一个名称setField的方法 因此如果有方法名重载了有可能报错
	 */
	public static boolean set(Object o, String field, Object value) {
		return set(o, field, value,true);
	}

	public static boolean set(Object o, String field, Object value, boolean ignoreCase) {
		if (o == null) {
			throw new RuntimeException("调用set时传入的对象为null");
		}

		if (o instanceof Map) {
			((Map) o).put(field, value);
			return true;
		}

		String set = "set" + StringUtil.firstUp(field);
		try {
			for (Method m : o.getClass().getMethods()) {
				boolean eq = false;
				if (ignoreCase) {
					eq = m.getName().equalsIgnoreCase(set);
				} else {
					eq = m.getName().equals(set);
				}

				if (eq) {
					m.invoke(o, value);
					return true;
				}
			}
			return false;
		} catch (Exception ex) {
			String valueClass=value==null?"null":value.getClass().getName();
			throw new RuntimeException("调用set出错:" + o.getClass().getName() + "." + field+",传入对象:"+valueClass, ex);
		}
	}

	public static Map toMap(Object bean) {
		try {
			Map<String, Object> returnMap = new HashMap<String, Object>();
			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();
				if (!propertyName.equals("class")) {
					Method readMethod = descriptor.getReadMethod();
					Object result = readMethod.invoke(bean, new Object[0]);
					if (result != null) {
						returnMap.put(propertyName, result);
					} else {
						returnMap.put(propertyName, "");
					}
				}
			}
			return returnMap;
		} catch (Exception ex) {
			throw new RuntimeException("将" + bean + "转为map的时候出错", ex);
		}
	}

	/** 获得一个类所有字段的类型 */
	public static Map<String, Class> getFieldTypes(Class clazz) {
		Set<String> fields = getSetFields(clazz);
		Map map = new HashMap();
		for (Method method : clazz.getMethods()) {
			String methodName = method.getName();
			if (methodName.equals("getClass")) {
				continue;
			}
			if (methodName.startsWith("get") && !methodName.equals("get")) {
				String name = getFieldName(method.getName());
				if (fields.contains(name)) {
					map.put(name, method.getReturnType());
				}
			}
		}
		return map;
	}

	/** 获得所有包含set方法 的属性名 */
	public static Set<String> getSetFields(Class clazz) {
		Set<String> fields = new HashSet();
		for (Method method : clazz.getMethods()) {
			String methodName = method.getName();

			if (method.getParameterTypes().length != 1) {
				continue;
			}
			if (methodName.startsWith("set") && !methodName.equals("set")) {
				String name = getFieldName(method.getName());
				fields.add(name);
			}
		}
		return fields;
	}

	/**
	 * 通过get或set方法名获得字段名称
	 * 
	 * @param methodName 方法名称
	 */
	public static String getFieldName(String methodName) {
		return StringUtil.firstLower(methodName.substring(3));
	}

	/** 根据类型设置一个类属性 */
	public static void setSimpleValue(Object bean, String name, String value, Class clazz) {
		if (clazz == String.class) {
			BeanUtil.set(bean, name, value);
			return;
		}
		if (value.equals("")) {
			return;
		}
		if (clazz == Date.class) {
			DateTime dt = new DateTime(value);
			BeanUtil.set(bean, name, dt);
		} else if (clazz == Integer.class || clazz == int.class) {
			BeanUtil.set(bean, name, Integer.parseInt(value));
		} else if (clazz == Long.class || clazz == long.class) {
			BeanUtil.set(bean, name, Long.parseLong(value));
		} else if (clazz == Double.class || clazz == double.class) {
			BeanUtil.set(bean, name, Double.parseDouble(value));
		} else if (clazz == Float.class || clazz == float.class) {
			BeanUtil.set(bean, name, Float.parseFloat(value));
		} else if (clazz == Boolean.class || clazz == boolean.class) {
			BeanUtil.set(bean, name, Boolean.parseBoolean(value));
		} else if (ClassUtil.isInstance(clazz, Enum.class)) {
			setEnum(bean, name, value, clazz);
		} else {
			throw new RuntimeException("不支持的类型:" + clazz);
		}
	}

	public static void setEnum(Object bean, String name, String value, Class clazz) {
		Object[] enums = null;
		try {
			enums = (Object[]) clazz.getMethod("values").invoke(clazz);
		} catch (Exception ex) {
			throw new RuntimeException("放心,不会到这的");
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
