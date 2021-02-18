package wang.wangby.persistence.file;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import wang.wangby.base.entity.Entity;
import wang.wangby.repostory.AbstractRepository;
import wang.wangby.repostory.dao.EntityDao;
import wang.wangby.utils.Integers;
import wang.wangby.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class FileRepository extends AbstractRepository {
    DataSerializer dataSerializer;
    String root;//根目录

    public FileRepository(DataSerializer dataSerializer, String root) throws IOException {
        this.dataSerializer = dataSerializer;
        if (!root.endsWith("/")) {
            root = root + "/";
        }
        this.root = root;
        Path dir = Paths.get(root);
        if (!dir.toFile().exists()) {
            Files.createDirectory(dir);
        }
    }

    @Override
    @SneakyThrows
    public <T extends Entity> T insert(T entity) {
        Long id = entity.id();
        if (id == null) {
            entity.id(newId());
        }

        Path dir = Paths.get(root + entity.getClass().getName());
        if (!dir.toFile().exists()) {
            Files.createDirectory(dir);
        }

        byte[] bs = dataSerializer.toByte(entity);
        Path path = getPath(entity.getClass(), entity.id());
        Files.createFile(path);
        Files.write(path, bs);
        return entity;
    }

    @Override
    public <T extends Entity> T init(T entity, String fieldNames) {
        throw new RuntimeException("未实现");
    }

    @Override
    public <T extends Entity> List<T> select(T entity, int offset, int limit) {
        List list = new ArrayList();
        Integers i=new Integers(0);
        this.iterator(entity.getClass(), a -> {
            if(i.get()<offset){
                return true;
            }
            if(i.get()>offset+limit){
                return false;
            }
            list.add(a);
            i.incrementAndGet();
            return true;
        });
        return list;
    }

    @Override
    public int delete(Class<? extends Entity> clazz, Long id) throws Exception {
        Path path = getPath(clazz, id);
        if (Files.deleteIfExists(path)) {
            return 1;
        }
        return 0;
    }

    @Override
    public <T extends Entity> int update(T model) throws Exception {
        Path path = getPath(model.getClass(), model.id());
        Files.delete(path);
        byte[] bs = dataSerializer.toByte(model);
        Files.write(path, bs);
        return 1;
    }

    @Override
    public <T extends Entity> T get(Class<T> clazz, Long id) throws Exception {
        Path path = getPath(clazz, id);
        if (!path.toFile().exists()) {
            return null;
        }
        byte[] bs = Files.readAllBytes(path);
        Object o = dataSerializer.toBean(bs, clazz);
        return (T) o;
    }

    private Path getPath(Class clazz, Long id) {
        String fileName = root + clazz.getName() + "/" + id;
        return Paths.get(fileName);
    }


    //遍历某个类
    @SneakyThrows
    public boolean iterator(Class clazz, Function<Entity, Boolean> visitor) {
        String clsName = clazz.getName();
        File file = new File(root + clsName);
        if (!file.isDirectory()) {
            return true;
        }
        for (String id : file.list()) {
            if (StringUtil.isEmpty(id)) {
                continue;
            }
            Path path = Paths.get(root + clsName + "/" + id);
            byte[] bs = Files.readAllBytes(path);
            Entity entity = (Entity) dataSerializer.toBean(bs, clazz);
            if (entity == null) {
                log.warn("数据不正确:path=" + path);
                continue;
            }
            if (!visitor.apply(entity)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public EntityDao getDao(Class<? extends Entity> entityClass) {
        throw new RuntimeException("未实现");
    }


}
