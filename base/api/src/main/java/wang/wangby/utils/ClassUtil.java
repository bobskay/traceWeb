package wang.wangby.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
public class ClassUtil{
	
	public static final Class[] NUMBER= {Integer.class,int.class,Long.class,long.class,Short.class,short.class};
	
	//一个java类是否是数字
	public static boolean isNumber(Class clazz) {
		for(Class n:NUMBER) {
			if(n==clazz) {
				return true;
			}
		}
		return false;
	}

	/** 将一个对象转换为日期 */
	public static Date toDate(Object o){
		if(StringUtil.isEmpty(o)){
			return null;
		}
		if(o instanceof Date){
			return (Date)o;
		}else if(o instanceof Timestamp){
			Timestamp t=(Timestamp)o;
			return new Date(t.getTime());
		}else if(o instanceof String){
			return new DateTime(o+"");
		}else{
			throw new RuntimeException("未知类型"+o.getClass()+",无法转为时间");
		}
	}

	/** 将一个简单类型数据替换成对象数组 */
	public static Object[] toObjectArray(Object obj){
		if(obj instanceof int[]){
			int[] o=(int[])obj;
			Integer[] is=new Integer[o.length];
			for(int i=0;i<o.length;i++){
				is[i]=o[i];
			}
			return is;
		}
		if(obj instanceof long[]){
			long[] o=(long[])obj;
			Long[] is=new Long[o.length];
			for(int i=0;i<o.length;i++){
				is[i]=o[i];
			}
			return is;
		}
		if(obj instanceof double[]){
			double[] o=(double[])obj;
			Double[] is=new Double[o.length];
			for(int i=0;i<o.length;i++){
				is[i]=o[i];
			}
			return is;
		}

		if(obj instanceof float[]){
			float[] o=(float[])obj;
			Float[] is=new Float[o.length];
			for(int i=0;i<o.length;i++){
				is[i]=o[i];
			}
			return is;
		}
		return (Object[])obj;
	}

	/** 获得方法签名里参数的泛型 */
	public static List<Type[]> getGenericTypeForArg(Method method){
		Type[] args=method.getGenericParameterTypes();
		List list=new ArrayList();
		for(int i=0;i<args.length;i++){
			if(args[i] instanceof ParameterizedType){
				Type[] types=((ParameterizedType)args[i]).getActualTypeArguments();
				list.add(types);
			}
		}
		return list;
	}

	public static  Class getFirstDeclareGenericType(Class clazz){
		Type[] types=clazz.getGenericInterfaces();
		for(Type type:types){
			Class t=getFirstGenericType(type);
			if(t!=null){
				return t;
			}
		}
		return null;
	}



	public static Class getFirstGenericType(Type parameterizedType){
		if(parameterizedType instanceof ParameterizedType){
			Type[] types=((ParameterizedType)parameterizedType).getActualTypeArguments();
			for(Type type:types){
				return (Class)type;
			}
		}
		return null;
	}

	/**
	 * 获得某个字段的声明所对应的泛型
	 * 
	 */
	public static Class getGenericType(Class clazz,String field){
		String methodName="get"+ StringUtil.firstUp(field);
		try{
			Method method=clazz.getMethod(methodName);
			// 通过返回值
			Type returnType=method.getGenericReturnType();
			if(returnType instanceof ParameterizedType){
				Type[] types=((ParameterizedType)returnType).getActualTypeArguments();
				for(Type type:types){
					return (Class)type;
				}
			}
			return null;
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(),ex);
		}
	}

	/** class a是否继承自class b */
	public static boolean isInstance(Class a,Class b){
		if(a==b){
			return true;
		}
		return b.isAssignableFrom(a);
	}

	/** 判断一个类是否是抽象抽象的，接口也算 */
	public static boolean isAbstrace(Class clazz){
		return Modifier.isAbstract(clazz.getModifiers());
	}

	/** 获得某个类所有实现的接口 */
	public static Set<Class> getAllInterface(Class clazz){
		Set set=new HashSet();
		addInterface(set,clazz);
		return set;
	}

	private static void addInterface(Set set,Class clazz){
		if(clazz.isInterface()){
			set.add(clazz);
		}
		Class parent=clazz.getSuperclass();
		if(parent!=null&&parent!=Object.class){
			addInterface(set,clazz);
		}
		for(Class in:clazz.getInterfaces()){
			addInterface(set,in);
		}
	}

	/** 将字符串转化为特定类型 */
	public static Object cast(String str,Class returnType){
		if(returnType==String.class){
			return str;
		}
		if(StringUtil.isEmpty(str)){
			return null;
		}
		if(returnType==Integer.class||returnType==int.class){
			return Integer.parseInt(str);
		}
		if(returnType==Long.class||returnType==long.class){
			return Long.parseLong(str);
		}
		if(returnType==Date.class){
			return new DateTime(str);
		}
		if(returnType==Boolean.class||returnType==boolean.class){
			return Boolean.parseBoolean(str);
		}
		throw new RuntimeException("目前不支持将String转换为:"+returnType.getName());
	}

	public static Integer toInt(Object obj){
		if(StringUtil.isEmpty(obj)){
			return null;
		}
		if(obj instanceof Integer){
			return (Integer)obj;
		}
		return Integer.parseInt(obj+"");
	}

	public static Long toLong(Object obj){
		if(obj==null|| StringUtil.isEmpty(obj)){
			return 0L;
		}
		if(obj instanceof Long){
			return (Long)obj;
		}
		return Long.parseLong(obj+"");
	}

	public static Double toDouble(Object obj){
		if(StringUtil.isEmpty(obj)){
			return 0D;
		}
		if(obj instanceof Double){
			return (Double)obj;
		}
		return Double.parseDouble(obj+"");
	}

	public static Short toShort(Object obj){
		if(obj==null|| StringUtil.isEmpty(obj)){
			return 0;
		}
		if(obj instanceof Short){
			return (Short)obj;
		}
		Double b=Double.parseDouble(obj+"");
		return b.shortValue();
	}

	public static <T> T toEnum(Class enumType,Object value){
		if(value instanceof String){
			return (T)Enum.valueOf(enumType,(String)value);
		}
		short i= ClassUtil.toShort(value);
		try{
			Object[] all=(Object[])enumType.getMethod("values").invoke(enumType);
			if(i>=all.length){
				log.error("无法将"+"{}转化为{}",i,enumType.getName());
				return null;
			}
			return (T)all[i];
		}catch(Exception ex){
			throw new RuntimeException("获取枚举列表出错:"+enumType,ex);
		}
	}

	public static Boolean toBoolean(Object obj){
		if(obj==null){
			return false;
		}
		if(obj instanceof Boolean){
			return (Boolean)obj;
		}
		if(obj instanceof String){
			if("Y".equals(obj)||"true".equals(obj)){
				return true;
			}
			if("N".equals(obj)||"false".equals(obj)){
				return false;
			}
			throw new RuntimeException("无法将"+obj+"转为bool类型");
		}

		long i=toLong(obj);
		return i>0;
	}

	public static <T> T newInstance(Class clazz){
		try{
			return (T)clazz.newInstance();
		}catch(Exception e){
			throw new RuntimeException("创建类的实例失败"+clazz,e);
		}
	}


	/** 判断一个对象是否数组 */
	public static boolean isArray(Object obj){
		return obj.getClass().isArray();
	}
	
	/** 判断一个对象是否数组 */
	public static boolean isArrayClass(Class clazz){
		if(clazz.isArray()) {
			return true;
		}
		return isInstance(clazz, Collection.class);
	}
	
	

	

	/** 获取类 静态属性 */
	public static <T> T getField(Class<T> clazz,String fieldName){
		try{
			Field field=clazz.getDeclaredField(fieldName);
			return (T)field.get(clazz);
		}catch(Exception e){
			throw new RuntimeException("找不到属性"+clazz.getName()+"."+fieldName,e);
		}
	}

	/** 通过注解获取属性 */
	public static List<Field> getFieldsByAnnotation(Class clazz,Class annotationClass){
		List list=new ArrayList();
		addField(list,clazz,annotationClass);
		return list;
	}

	public static <T> T getAnnotation(Field field,Class ann){
		return (T)field.getAnnotation(ann);
	}

	public static <T> T getAnnotation(Class clazz,Class<T> ann){
		if(clazz==null||clazz==Class.class){
			return null;
		}
		T o=(T)clazz.getAnnotation(ann);
		if(o!=null)
			return o;
		return getAnnotation(clazz.getSuperclass(),ann);
	}

	public static <T> T getAnnotation(Class clazz,String methodName,Class ann){
		if(clazz==null||clazz==Class.class){
			return null;
		}
		Method md=getFirstMethod(clazz,methodName);
		if(md==null){
			return getAnnotation(clazz.getSuperclass(),methodName,ann);
		}
		Annotation an=md.getAnnotation(ann);
		if(an!=null){
			return (T)an;
		}
		return getAnnotation(clazz.getSuperclass(),methodName,ann);
	}

	/** 通过注解获取方法 */
	public static List<Method> getMethodsByAnnotation(Class clazz,Class annotationClass){
		List list=new ArrayList();
		addMethod(list,clazz,annotationClass);
		return list;
	}

	private static void addMethod(List methods,Class clazz,Class annotationClass){
		for(Method method:clazz.getDeclaredMethods()){
			Annotation an=method.getAnnotation(annotationClass);
			if(an!=null){
				methods.add(method);
			}
		}
		Class parent=clazz.getSuperclass();
		if(parent!=Object.class&&parent!=Class.class){
			addMethod(methods,parent,annotationClass);
		}
	}

	private static void addField(List<Field> fields,Class clazz,Class annotationClass){
		for(Field f:clazz.getDeclaredFields()){
			Annotation an=f.getAnnotation(annotationClass);
			if(an!=null){
				fields.add(f);
			}
		}
		Class parent=clazz.getSuperclass();
		if(parent!=Object.class){
			addField(fields,parent,annotationClass);
		}
	}

	/** 获取某个类的字段信息 */
	public static Field getFieldInfo(Class clazz,String fieldName){
		try{
			Field field=clazz.getDeclaredField(fieldName);
			return field;
		}catch(Exception e){
			throw new RuntimeException("找不到属性"+clazz.getName()+"."+fieldName,e);
		}
	}

	// 获得某个类的第一个符合名称的方法
	public static Method getFirstMethod(Class clazz,String mName){
		for(Method m:clazz.getMethods()){
			if(m.getName().equals(mName)){
				return m;
			}
		}
		return null;
	}

	/**
	 * 根据class文件所在路径获取对应的class
	 * 
	 * @param classPath 类坐在路径
	 * @param classPath 类所在包前缀
	 * 
	 */
	public static Class toClass(String classPath,String pkgPix){
		String path=classPath.replace('/','.').replace('\\','.');
		int idx=path.indexOf(pkgPix);
		if(idx==-1){
			log.error("无法通过文件路径获取包名:"+path+"-->"+pkgPix,new Exception());
			return null;
		}
		String className=path.substring(idx,path.length()-".class".length());
		try{
			return Class.forName(className);
		}catch(ClassNotFoundException e){
			log.error("创建类("+className+")失败:"+path+"-->"+pkgPix,e);
			return null;
		}
	}

	public static boolean isPrimitive(Class clazz) {
		if(clazz.getName().startsWith("java.")) {
			return true;
		}
		if(clazz.getName().indexOf('.')==-1) {
			return true;
		}
		return false;
	}

	public static Class getTargetClass(Object obj){
		return getOriginal(obj.getClass());
	}

	//获得被代理的类
	public static Class getOriginal(Class clazz){
		for(Class it:clazz.getInterfaces()){
			if(it.getName().equals("org.springframework.aop.SpringProxy")){
				return getOriginal(clazz.getSuperclass());
			}
		}
		return clazz;
	}

    public static List<Field> getAllFields(Class aClass) {
		List list=new ArrayList();
		addFields(aClass,list);
		return list;
    }

	private static void addFields(Class aClass,List list) {
		for(Field f :aClass.getDeclaredFields()){
			list.add(f);
		}
		if(aClass.getSuperclass()!=Object.class){
			addFields(aClass.getSuperclass(),list);
		}
	}

	public static <T> void addAnnotations(Class clazz, Class<T> annClass,List list) {
		for(Annotation ann:clazz.getAnnotations()){
			if(ClassUtil.isInstance(ann.getClass(),annClass)){
				list.add(ann);
			}
		}
		Class parent=clazz.getSuperclass();
		if(parent!=Object.class){
			addAnnotations(parent,annClass,list);
		}
	}

	public static <T> List<T> getAnnotations(Class clazz, Class<T> annClass) {
		List list=new ArrayList();
		addAnnotations(clazz,annClass,list);
		return list;
	}
}
