package wang.wangby.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import wang.wangby.persistence.file.AsynRepository;
import wang.wangby.persistence.file.DataSerializer;
import wang.wangby.persistence.file.FileRepository;
import wang.wangby.persistence.file.config.MyFileProperties;
import wang.wangby.repostory.Repository;

import java.io.IOException;
import java.util.Set;

@Configuration
@ConditionalOnClass(FileRepository.class)
@Import(BaseToolAutoConfiguration.class)
@EnableConfigurationProperties(MyFileProperties.class)
public class FileRepositoryAutoConfiguration {

    @Bean
    public Repository fileRepository(MyFileProperties myFileProperties, Beans beans) throws IOException {
        Set entitySet = beans.getEntities();
        DataSerializer dataSerializer = new DataSerializer();
        FileRepository fileDao = new FileRepository(dataSerializer, myFileProperties.getDataDir());
        if (!myFileProperties.isAsync()) {
            return fileDao;
        }
        AsynRepository repository = new AsynRepository(fileDao, entitySet, myFileProperties.getMaxQueueSize(), myFileProperties.getPeriodSecond());
        return repository;
    }
}
