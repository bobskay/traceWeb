package wang.wangby.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import wang.wangby.base.entity.convertor.EntityTableConvertor;
import wang.wangby.repostory.config.RepostoryProperties;
import wang.wangby.repostory.dao.EntityDao;
import wang.wangby.template.TemplateUtil;
import wang.wangby.utils.ClassUtil;
import wang.wangby.utils.FileUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class RepositoryConfigHelper {
	List<Resource> mapperedResources;
	DataSource dataSource;

	public RepositoryConfigHelper(DataSource dataSource, RepostoryProperties repostoryProperties, TemplateUtil templateUtil, EntityTableConvertor entityTableConvertor) throws IOException {
		String text = FileUtil.getText(MapperCreator.class, repostoryProperties.getMapperTemplate());
		MapperCreator mapperCreator = new MapperCreator(templateUtil, repostoryProperties.getTempDir(),
				text, repostoryProperties.getDaoBasePackage(), entityTableConvertor::getTableName);
		this.dataSource=dataSource;
		this.mapperedResources=getMybatisMapperResource(mapperCreator,repostoryProperties.getMybatisMapperLocations());
	}

	public SqlSessionFactory createSqlSessionFactory()
			throws Exception {
		log.debug("使用自动配置的SqlSessionFactory");
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource);
		for(Resource r: mapperedResources) {
			log.debug("发现mybatis映射文件:{}",r);
		}
		factory.setMapperLocations(mapperedResources.toArray(new Resource[] {}));
		return factory.getObject();
	}

	//获取所有mybatis的映射文件
	private List<Resource> getMybatisMapperResource(MapperCreator creator,String mybatisMapperLocations) throws IOException {
		List<Resource> result = new ArrayList();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		String base = creator.getBasePackage().replace(".", "/");
		Resource[] allClass = resolver.getResources("classpath*:" + base + "/**/*.class");
		log.debug("开始扫描所有dao:" + base);
		for (int i = 0; i < allClass.length; i++) {
			Resource res = allClass[i];
			try {
				String xml = createMapperXml(res, creator);
				if (xml == null) {
					continue;
				}
				ByteArrayResource bs = new ByteArrayResource(xml.getBytes(), FileUtil.ENCODE) {
					public String toString() {
						return res + "";
					}
				};
				result.add(bs);
			} catch (Exception ex) {
				log.warn("自动生成" + res + "的映射出错:" + ex.getMessage(), ex);
			}
		}

		log.debug("自动生成映射文件个数:" + result.size());
		Resource[] mybatisMapper = resolver.getResources(mybatisMapperLocations);
		log.debug("找到手动配置的文件个数:"+mybatisMapper.length);

		result.addAll(Arrays.asList(mybatisMapper));
		return result;
	}

	private String createMapperXml(Resource res, MapperCreator creator) throws Exception {
		Class daoClass = getDaoClass(res, creator);
		if (daoClass == null) {
			return null;
		}
		return creator.getMapperXml(daoClass);
	}

	/**
	 * 获取和数据库对应的dao类 1.查找所有prefix包下的类 2.选出继承BaseDao的 3.配置了Mapper的
	 */
	private Class getDaoClass(Resource res, MapperCreator creator) throws IOException, ClassNotFoundException {
		String prefix = creator.getBasePackage().replace(".", "/") + "/";
		String url = res.getURL().getPath();
		int beginIndex = url.lastIndexOf(prefix);
		if (beginIndex == -1) {
			return null;
		}
		int end = url.indexOf(".", beginIndex);
		String className = url.substring(beginIndex, end).replace("/", ".");
		Class daoClass = Class.forName(className);
		if (daoClass == EntityDao.class) {
			return null;
		}

		if (!ClassUtil.isInstance(daoClass, EntityDao.class)) {
			return null;
		}
		Mapper mapper = AnnotationUtils.getAnnotation(daoClass, Mapper.class);
		if (mapper == null) {
			return null;
		}

		return daoClass;
	}
}
