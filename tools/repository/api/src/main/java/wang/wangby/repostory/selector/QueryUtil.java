package wang.wangby.repostory.selector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import wang.wangby.Constants;
import wang.wangby.base.entity.Entity;
import wang.wangby.base.entity.EntityField;
import wang.wangby.base.entity.EntityInfo;
import wang.wangby.repostory.EntityDaoFinder;
import wang.wangby.repostory.dao.EntityDao;
import wang.wangby.utils.ClassUtil;
import wang.wangby.utils.StrBuilder;
import wang.wangby.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QueryUtil {
    public static final String OFFSET = Constants.OFFSET;
    public static final String LIMIT = Constants.LIMIT;
    @Autowired
    private EntityDaoFinder entityDaoFinder;
    public QueryUtil( ) {

    }

    public QueryUtil(EntityDaoFinder entityDaoFinder) {
        this.entityDaoFinder = entityDaoFinder;
    }


    public Selector newSelector(Class<? extends Entity> clazz) {
        Entity query = ClassUtil.newInstance(clazz);
        EntityDao dao = entityDaoFinder.getDao(clazz);
        if (dao == null) {
            throw new RuntimeException("找不到" + clazz.getName() + "对应的mapper");
        }
        return new Selector(dao, query);
    }

    public void fill(Object obj, String fileNames) {
        if (obj == null) {
            return;
        }
        String fName = StringUtil.getFirstBefore(fileNames, ".");
        String remain = StringUtil.getFirstAfter(fileNames, ".");

        if (fileNames.indexOf(".") == -1) {
            remain = "";
        }
        if (obj instanceof List) {
            List<Entity> list = (List) obj;
            Object values = fill(list, fName);
            if (StringUtil.isNotEmpty(remain)) {
                fill(values, remain);
            }
        } else {
            Object values = fill((Entity) obj, fName);
            if (StringUtil.isNotEmpty(remain)) {
                fill(values, remain);
            }
        }
    }


    private Object fill(Entity entity, String fName) {
        EntityField field = EntityInfo.getInstance(entity.getClass()).getField(fName);
        if (field == null) {
            throw new RuntimeException("找不到字段" + entity.getClass().getName() + "." + fName);
        }
        if (field.isList()) {
            return fillList(entity, field);
        }
        Object key = field.getFkField().get(entity);
        Object value = newSelector(field.getType()).attr(field.getReferenceName()).eq(key).get();
        if (value != null) {
            field.set(entity, value);
        }
        return value;
    }


    //通过关联查询,填充list的某个字段
    private Object fill(List<? extends Entity> list, String fName) {
        if (list.size() == 0) {
            return null;
        }
        EntityInfo entityInfo = EntityInfo.getInstance(list.get(0).getClass());
        EntityField field = entityInfo.getField(fName);
        if (field == null) {
            throw new RuntimeException("找不到字段:" + entityInfo.getEntityClass().getSimpleName() + "." + fName);
        }
        if (field.isList()) {
            return fillList(list, field);
        }
        List search = field.getFkField().getList(list);
        List<Entity> values = newSelector(field.getRealClass()).attr(field.getReferenceName()).in(search).list();
        EntityField childField = field.getReferenceField();
        for (Entity parent : list) {
            Object key = field.getFkField().get(parent);
            for (Entity child : values) {
                if (key.equals(childField.get(child))) {
                    parent.set(field.getName(), child);
                    break;
                }
            }
        }
        return values;
    }

    private Object fillList(Entity entity, EntityField field) {
        Object key = field.getFkField().get(entity);
        List value = newSelector(field.getRealClass()).attr(field.getReferenceName()).eq(key).list();
        entity.set(field.getName(), value);
        return value;
    }

    private Object fillList(List<? extends Entity> list, EntityField field) {
        List search = field.getFkField().getList(list);
        List<Entity> values = newSelector(field.getRealClass()).attr(field.getReferenceName()).in(search).list();
        EntityField childField = field.getReferenceField();
        for (Entity parent : list) {
            Object key = field.getFkField().get(parent);
            List children = (List) parent.get(field.getName());
            if (children == null) {
                children = new ArrayList();
                parent.set(field.getName(), children);
            }
            for (Entity child : values) {
                if (key.equals(childField.get(child))) {
                    children.add(child);
                }
            }
        }
        return values;
    }


    public static String in(String name, List values) {
        if (values.size() == 0) {
            return "1<>1";
        }
        StrBuilder sb = new StrBuilder();
        sb.append(name + " in (");
        for (Object o : values) {
            if (o.getClass() == String.class) {
                sb.append("'" + o + "',");
                continue;
            }
            if (o.getClass() == Long.class || o.getClass() == Integer.class || o.getClass() == Short.class) {
                sb.append(o + ",");
                continue;
            }
            if (o.getClass().getName().startsWith("java.lang")) {
                throw new RuntimeException("目前不支持通过该类型查询:" + o.getClass());
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.append(")").toString();
    }
}
