package wang.wangby.repostory.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import wang.wangby.annotation.Remark;

@Data
@ConfigurationProperties(prefix = "my.repostory")
public class RepostoryProperties {

	@Remark("生成映射文件的模板")
	private String mapperTemplate = "/codetemplate/mapper.xml.vm";
	@Remark("mybatis的映射文件路径")
	private String   mybatisMapperLocations= "classpath:mybatis/mapper/**/*.xml";
	//生成mapping文件扫描的跟路径路径
	private String daoBasePackage = "wang.wangby";
	//系统会根据entity类自动生成mybatis的映射文件,如果是开发环境并且tempDir不为空,就在临时文件夹里生成实际文件
	private String tempDir = "/opt/temp";
	//机器号,一个集群里面需要为机器分配唯一序列号用于生成UUID
	private Integer machineNo = 0;

}
