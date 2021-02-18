package wang.wangby.tools.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.web.Menu;
import wang.wangby.entity.request.Response;
import wang.wangby.model.vo.JavaClass;
import wang.wangby.model.vo.JavaField;
import wang.wangby.utils.StringUtil;

import java.util.*;

@RestController
@RequestMapping("json2Java")
public class Json2JavaController extends ToolController {

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping("/index")
    @Menu("json数据转java")
    public String index() {
        return $("index");
    }

    @RequestMapping("toJava")
    public String toJava(String packageName, String rootClass, String json) {

        List<JavaClass> javaClassList = json2Java(packageName, rootClass, json.trim());
        Map map = new HashMap();
        map.put("javaClassList", javaClassList);

        return $("toJava", map);
    }

    List<JavaClass> json2Java(String packageName, String rootClass, String json) {
        List list = new ArrayList();
        if (json.startsWith("[")) {
            JSONArray jsonArray = jsonUtil.toBean(json, JSONArray.class);
            if (jsonArray.size() == 0) {
                return new ArrayList<>();
            }
            for (Object o : jsonArray) {
                if (o instanceof JSONObject) {
                    addClass((JSONObject) o, packageName, rootClass, list);
                }
            }
            return list;
        }
        JSONObject obj = jsonUtil.toBean(json, JSONObject.class);
        addClass(obj, packageName, rootClass, list);
        return list;
    }

    /**
     * @return  是否添加成功
     *  1. 如果对象里的属性名称不符合java规范就不添加
     *  2. 如果对象的字段个数为0也不添加
     * */
    private boolean addClass(JSONObject jsonObject, String pkgName, String clasName, List<JavaClass> allClass) {
        JavaClass javaClass = new JavaClass();
        javaClass.setPackageName(pkgName);
        javaClass.setName(clasName);

        Set<Map.Entry<String, Object>> entrys = jsonObject.entrySet();
        if (entrys.size() == 0) {
            return false;
        }
        Set<String> improts=new HashSet<>();
        improts.add(Data.class.getName());

        List<JavaField> fieldList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : entrys) {
            String name = StringUtil.firstLower(entry.getKey());
            if(!name.matches(StringUtil.REG_NAMING_CONVENTION)){
                return false;
            }

            JavaField f = new JavaField();
            f.setName(name);
            fieldList.add(f);

            Object value = entry.getValue();
            if (value instanceof JSONObject) {
                JSONObject js = (JSONObject) value;
                if (js.size() != 0) {
                    String className = StringUtil.firstUp(f.getName());
                    boolean succes=addClass(js, pkgName, className, allClass);
                    if(!succes){
                        improts.add(HashMap.class.getName());

                        f.setTypeName("HashMap");
                    }else{
                        f.setTypeName(className);
                    }
                } else {
                    f.setTypeName("String");
                }
                continue;
            }

            if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                addArray(array, f, pkgName, allClass);
                improts.add(List.class.getName());
                continue;
            }

            String type = getType(value);
            f.setTypeName(type);
        }
        javaClass.setImportList(new ArrayList<>(improts));
        javaClass.setJavaFieldList(fieldList);
        allClass.add(javaClass);
        return true;
    }

    private void addArray(JSONArray array, JavaField field, String pkgName, List<JavaClass> allClass) {
        if (array.size() == 0) {
            field.setTypeName("List");
            return;
        }
        Object o = array.get(0);
        if (!(o instanceof JSONObject)) {
            field.setTypeName("List<" + getType(o) + ">");
            return;
        }
        JSONObject js = (JSONObject) o;
        if (js.size() == 0) {
            field.setTypeName("List<" + getType(o) + ">");
            return;
        }
        String typeName = StringUtil.firstUp(field.getName());
        if (typeName.endsWith("s")) {
            typeName = typeName.substring(0, typeName.length() - 1);
        }
        addClass(js, pkgName, typeName, allClass);
        field.setTypeName("List<" + typeName + ">");
    }

    private String getType(Object value) {
        if (value == null) {
            return "String";
        }
        return value.getClass().getSimpleName();
    }


    @Data
    public class name {
        private String name;
    }

    @Remark("格式化js")
    @RequestMapping("format")
    public Response<String> format(String json){
        JSONObject jsonObject= JSON.parseObject(json);
        String js=jsonUtil.toFormatString(jsonObject);
        return respone(js);
    }
}
