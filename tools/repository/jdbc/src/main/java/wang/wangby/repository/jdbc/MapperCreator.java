package wang.wangby.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import wang.wangby.repostory.config.RepostoryProperties;
import wang.wangby.repostory.dao.EntityDao;
import wang.wangby.template.TemplateUtil;
import wang.wangby.utils.FileUtil;
import wang.wangby.utils.StringUtil;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class MapperCreator {

	private TemplateUtil templateUtil;
	private String outpurDir;
	private String templateText;
	private String basePackage;
	private Function<Class,String> getTableName;

	public MapperCreator(TemplateUtil templateUtil,String outpurDir,String templateText,String basePackage ,Function<Class,String> getTableName) {
		this.templateUtil = templateUtil;
		this.outpurDir = outpurDir;
		this.templateText=templateText;
		this.basePackage=basePackage;
		this.getTableName=getTableName;
		if (StringUtil.isNotEmpty(outpurDir)) {
			try {
				Files.createDirectories(Paths.get(outpurDir));
			} catch (IOException e) {
				log.warn("创建目录失败:"+outpurDir,e);
				this.outpurDir = null;
			}
		}
	}

	//通过传入的baseDao生成映射
	public String getMapperXml(Class<? extends EntityDao> daoClass) throws Exception {
		Type[] ptClass = daoClass.getGenericInterfaces();
		if (ptClass.length != 1) {
			log.warn("忽略class:"+ daoClass.getName());
			return null;
		}
		ParameterizedType type = (ParameterizedType) ptClass[0];
		Type t = type.getActualTypeArguments()[0];
		Class entity = (Class) t;

		Map map = toRootMap(daoClass, entity);

		log.info("准备生成mapper.xml:" + daoClass.getName() + "<" + entity.getName() + ">");

		String xml = templateUtil.parseText(templateText, map);
		if (StringUtil.isNotEmpty(outpurDir)) {
			String name =outpurDir + "/" + entity.getSimpleName() + "Mapper.xml";
			FileUtil.createFile(name, xml);
			log.info("生成文件:" + name);
		} else {
			log.debug("生成映射文件\n{}", xml);
		}

		return xml;
	}

	private Map toRootMap(Class daoClass, Class entityClass) {
		Map map = new HashMap();
		map.put("daoClass", daoClass.getName());
		map.put("entityClass", entityClass.getName());
		map.put("help", new MapperCreatHelper(entityClass,getTableName.apply(entityClass)));
		return map;
	}

	public String getBasePackage() {
		return basePackage;
	}
	
	public static MapperCreator getDefault(){
		TemplateUtil templateUtil=new TemplateUtil();
		RepostoryProperties properties=new RepostoryProperties();
		String text= FileUtil.getText(MapperCreator.class,properties.getMapperTemplate());
		MapperCreator creator=new MapperCreator(templateUtil,properties.getTempDir(),
				text,properties.getDaoBasePackage(), c->c.getSimpleName().toLowerCase());
		return creator;
	}

}
