package wang.wangby.utils;

import wang.wangby.function.Equals;
import wang.wangby.function.Setter;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;


public class CollectionUtil{
	public static List toList(Object[] objs){
		List list=new ArrayList();
		for(int i=0;i<objs.length;i++){
			list.add(objs[i]);
		}
		return list;
	}

	public static List sort(List list,final String field){
		Collections.sort(list,new MyComparator(field));
		return list;
	}

	public static <T> List<T> sort(List<T> list,Function<T,Comparable> field){
		Collections.sort(list,(a,b)->{
			Comparable ca=field.apply(a);
			Comparable cb=field.apply(b);
			if(ca==null){
				return -1;
			}
			if(cb==null){
				return 1;
			}
			return ca.compareTo(cb);
		});
		return list;
	}
	
	public static <From,To> List<To> convert(List<From> from,Function<From, To> convertor) {
		List list=new ArrayList();
		for(From f:from) {
			To to=convertor.apply(f);
			list.add(to);
		}
		return list;
	}

	//提取list里的某个字段用connector连接
	public static String pickUpField(List list,String field,String connector){
		StringBuilder sb=new StringBuilder();
		if(list==null||list.size()==0){
			return "";
		}
		for(Object o:list){
			Object v= BeanUtil.get(o,field);
			sb.append(v+connector);
		}
		return sb.substring(0,sb.length()-connector.length());
	}

	//提取list对象里的某个字段值
	public static List pickUpField(List list,String field){
		if(list==null){
			return new ArrayList();
		}
		List result=new ArrayList();
		for(Object o:list){
			Object v= BeanUtil.get(o,field);
			result.add(v);
		}
		return result;
	}

	public static <T,R> List<R> pickUp(Collection<T> list,Function<T,R> fun){
		if(list==null){
			return new ArrayList();
		}
		List result=new ArrayList();
		for(T t:list){
			result.add(fun.apply(t));
		}
		return result;
	}

	//提取list里面么某个field对象的字段(value)值
	public static List pickUp(Collection list,String field,Object value){
		if(list==null){
			return new ArrayList();
		}
		List result=new ArrayList();
		for(Object o:list){
			Object v= BeanUtil.get(o,field);
			if(value.equals(v)){
				result.add(o);
			}
		}
		return result;
	}

	public static class MyComparator implements Comparator,Serializable{
		public String compareField;

		public MyComparator(String compareField){
			this.compareField=compareField;
		}

		public int compare(Object o1,Object o2){
			int result=0;
			Object obj1= BeanUtil.get(o1,compareField);
			Object obj2= BeanUtil.get(o2,compareField);
			if(obj1==null){
				return -1;
			}else if(obj2==null){
				return 1;
			}else if(obj1 instanceof Date){
				Date d1=(Date)obj1;
				Date d2=(Date)obj2;
				return d1.compareTo(d2);
			}else if(obj1 instanceof Integer){
				Integer d1=(Integer)obj1;
				Integer d2=(Integer)obj2;
				result=(d1-d2);
			}else{
				result=obj1.toString().compareTo(obj2.toString());
			}
			return result;
		}

	}

	/** 将list转为map */
	public  static <T,K> Map<K,T> toMap(Collection<T> list,Function<T,K> getKey){
		if(list==null){
			return new HashMap();
		}
		Map map=new HashMap();
		for(T o:list){
			K k=getKey.apply(o);
			map.put(k,o);
		}
		return map;
	}


	/**
	 * 将第二个list的值放到第一个里面
	 * @param setter 第一个list里存第二个list的set方法
	 * @param  equal 判断是否符合
	 * */
	public static  <P,C> void setList(List<P> parent, List<C> children, Setter<P,List> setter, Equals<P,C> equal){
		for(P o:parent){
			List list=new ArrayList();
			for(C c:children){
				if(equal.match(o,c)){
					list.add(c);
				}
			}
			setter.set(o,list);
		}
	}

	/**
	 * 将第二个list里的对象放到第一个里
	 */
	public  static <P,C> void set(List<P> parents,List<C> child,Setter<P,C> setter,Equals<P,C> equals){
		for(int i=0;i<parents.size();i++){
			P parent=parents.get(i);
			for(C ch:child){
				if(equals.match(parent,ch)){
					setter.set(parent,ch);
					break;
				}
			}
		}
	}

	public static List singleList(Object object) {
		List list=new ArrayList();
		list.add(object);
		return list;
	}

}
