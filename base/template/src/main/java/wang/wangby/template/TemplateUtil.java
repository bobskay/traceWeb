package wang.wangby.template;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import wang.wangby.log.LogUtil;
import wang.wangby.template.velocity.MyUberspect;
import wang.wangby.utils.FileUtil;
import wang.wangby.utils.StringUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;


/**模板工具类,在VelocityemplateAutoConfiguration配置*/
@Slf4j
public class TemplateUtil{

	private VelocityEngine velocityEngine;
	private String root;

	public TemplateUtil(){
		this(new VelocityProperties(),null);
	}

	public TemplateUtil(VelocityProperties velocityProperties,SimpleObjectConvertor convertor){
		SimpleObject.init(convertor);
		VelocityEngine velocityEngine = getVelocityEngine(velocityProperties);
		this.velocityEngine=velocityEngine;
		this.root=velocityProperties.getRoot();
	}

	private static VelocityEngine getVelocityEngine(VelocityProperties velocityProperties) {
		VelocityEngine velocityEngine = new VelocityEngine();
		Properties p = new Properties();
		p.setProperty("file.resource.loader.class", ClasspathResourceLoader.class.getName());
		p.setProperty("userdirective", TrimAllDirective.class.getName());
		p.setProperty("input.encoding", velocityProperties.getEncoding());
		p.setProperty("output.encoding", velocityProperties.getEncoding());
		p.setProperty("runtime.introspector.uberspect", MyUberspect.class.getName());

		p.setProperty("runtime.log", velocityProperties.getLog());
		velocityEngine.init(p);
		log.info("veclocity自动配置完成,配置信息:{}", velocityProperties);
		return velocityEngine;
	}

	/** 创建velocity的上下文 */
	private VelocityContext mapToContext(Map map){
		VelocityContext context=new VelocityContext();
		context.put("pound","#");
		context.put("dollar","$");
		if(map==null) {
			return context;
		}
		Set<Entry> en=map.entrySet();
		for(Entry e:en){
			if(e.getKey()==null){
				log.warn("解析模板时传入的key为null:{}",map,new Exception());
				continue;
			}
			context.put(e.getKey().toString(),e.getValue());
		}
		return context;
	}

	/**
	 * 即系模板
	 * 
	 * @param text 模板内容
	 * @param map 模板里的参数
	 */
	public String parseText(String text,Map map){
		VelocityContext context=mapToContext(map);
		StringWriter wr=new StringWriter();
		try{
			velocityEngine.evaluate(context,wr,"",text);
		}catch(Exception ex){
			log.error("解析模板出错，模板内容："+text, LogUtil.getCause(ex));
			throw new RuntimeException("解析字符串模板出错,模板内容\n"+text,ex);
		}
		return wr.toString();
	}

	/**
	 * 解析某个模板
	 * 
	 * @param path 模板地址,模板默认的根路径为/templates,所以所有模板必须放在这个目录下
	 * @param map 参数
	 */
	public String parseTemplate(String path,Map map){
		if(!path.startsWith("/")) {
			path="/"+path;
		}
		VelocityContext context=mapToContext(map);
		Template template=velocityEngine.getTemplate(root+path);
		StringWriter writer=new StringWriter();
		template.merge(context,writer);
		String str=writer.toString();
		writer.flush();
		return str;
	}

	//获得模板内容
	public String getTemplateContent(String path) throws IOException{
		if(!path.startsWith("/")) {
			path="/"+path;
		}
		return FileUtil.getText(TemplateUtil.class, root+path);
	}

	public void writer(String path,Map map,Writer writer){
		VelocityContext context=mapToContext(map);
		Template template=velocityEngine.getTemplate(root+path);
		template.merge(context,writer);
	}

	/**
	 * 将对象放入模版时根据对象类,获得默认的名称
	 * 内部类不能用这个方法
	 */
	public static String defaultName(Object obj){
		if(obj==null){
			return null;
		}

		// 此处不可能执行到,除非controller的返回值是类似Object[]{Object[]}的多层嵌套
		if(obj instanceof Object[]){
			throw new RuntimeException("传入的的是object[],无法获得对象名");
		}
		if(obj instanceof List){
			List list=(List)obj;
			if(list.size()==0){
				return null;
			}else{
				Object first=list.get(0);
				if(first==null){
					log.warn("要填充的模板参数list值为null",new Exception());
					return "errorList";
				}
				String name= StringUtil.firstLower(list.get(0).getClass().getSimpleName());
				name= StringUtil.getFirstBefore(name,"$");
				return name+"List";
			}
		}
		String name= StringUtil.firstLower(obj.getClass().getSimpleName());
		return StringUtil.getFirstBefore(name,"$");
	}

	public String getRoot(){
		return root;
	}
}
