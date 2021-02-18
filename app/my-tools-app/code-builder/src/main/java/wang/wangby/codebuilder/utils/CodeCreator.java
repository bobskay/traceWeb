package wang.wangby.codebuilder.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.api.Param;
import wang.wangby.annotation.api.Return;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.annotation.persistence.Length;
import wang.wangby.annotation.persistence.Table;
import wang.wangby.annotation.web.Menu;
import wang.wangby.base.entity.Entity;
import wang.wangby.codebuilder.controller.vo.CodeCreateResult;
import wang.wangby.entity.Pagination;
import wang.wangby.entity.request.Response;
import wang.wangby.exception.Message;
import wang.wangby.model.vo.JavaClass;
import wang.wangby.model.vo.JavaField;
import wang.wangby.repostory.database.SqlUtil;
import wang.wangby.repostory.database.dto.ColumnInfo;
import wang.wangby.repostory.database.dto.TableInfo;
import wang.wangby.utils.StringUtil;
import wang.wangby.web.controller.BaseController;

import java.util.*;

@Getter
public class CodeCreator {
    //表信息
    TableInfo tableInfo;
    //基准包
    String packageName;
    //model名称,首字母小写
    String modelName;
    //主键信息
    JavaField pkField;

    public CodeCreator(String sql, String packageName) {
        tableInfo = SqlUtil.toTable(sql);
        this.packageName = packageName;
        modelName = StringUtil.getFirstAfter(tableInfo.getTableName(), "_");
        for (ColumnInfo columnInfo : tableInfo.getColumns()) {
            if (columnInfo.getIsPk()) {
                pkField = toJava(columnInfo, new HashSet<>());
                return;
            }
        }
        throw new Message("找不到该表的主键:" + tableInfo.getTableName());
    }


    public JavaClass model() {
        JavaClass model = empty(CodeType.model);
        model.setName(StringUtil.firstUp(modelName));
        List<JavaField> fieldList = new ArrayList<>();

        Set<String> importSet = new HashSet<>();
        importSet.add(lombok.Data.class.getName());
        importSet.add(Entity.class.getName());
        importSet.add(Table.class.getName());


        for (ColumnInfo col : tableInfo.getColumns()) {
            //主键放在第一位
            JavaField field = toJava(col, importSet);
            if (col.getIsPk()) {
                fieldList.add(0, field);
            } else {
                fieldList.add(field);
            }
        }
        model.setJavaFieldList(fieldList);
        model.setImportList(new ArrayList<>(importSet));
        return model;
    }

    public JavaField toJava(ColumnInfo col, Set<String> importSet) {
        JavaField field = new JavaField();
        Class claz = javaType(col);
        field.setTypeName(claz.getSimpleName());
        if (!claz.getName().startsWith("java.lang")) {
            importSet.add(claz.getName());
        }
        field.setName(col.getColumnName());
        String comment = col.getColumnComment();
        if (comment == null) {
            comment = "";
        }
        comment = StringUtil.getFirstBefore(comment, ",");
        field.setRemark(comment);
        List<String> ann = new ArrayList<>();
        if (col.getIsPk()) {
            ann.add("@Id");
            importSet.add(Id.class.getName());
        }
        if (col.getMaxLength() != null) {
            ann.add("@Length(" + col.getMaxLength() + ")");
            importSet.add(Length.class.getName());
        }
        if (StringUtil.isNotEmpty(col.getColumnComment())) {
            ann.add("@Remark(\"" + col.getColumnComment() + "\")");
            importSet.add(Remark.class.getName());
        }
        field.setAnnotation(ann);
        return field;
    }

    public Class javaType(ColumnInfo columnInfo) {
        if ("bigint".equalsIgnoreCase(columnInfo.getDataType())) {
            return Long.class;
        }
        if ("int".equalsIgnoreCase(columnInfo.getDataType())) {
            return Integer.class;
        }
        if ("tinyint".equalsIgnoreCase(columnInfo.getDataType())) {
            return Integer.class;
        }
        if ("dateTime".equalsIgnoreCase(columnInfo.getDataType())) {
            return Date.class;
        }
        return String.class;
    }

    private JavaClass empty(CodeType type) {
        JavaClass javaClass = new JavaClass();
        javaClass.setPackageName(type.pkgName(packageName));
        javaClass.setName(type.className(modelName));
        javaClass.setImportList(new ArrayList<>());
        javaClass.setPkField(pkField);
        return javaClass;
    }

    public JavaClass service() {
        JavaClass service = empty(CodeType.service);
        service.getImportList().add(Autowired.class.getName());
        service.getImportList().add(Service.class.getName());
        service.getImportList().add("wang.wangby.dao.BaseDao");
        service.getImportList().add("wang.wangby.service.BaseService");
        service.getImportList().add(CodeType.dao.fillName(packageName, modelName));
        service.getImportList().add(CodeType.model.fillName(packageName, modelName));
        return service;
    }


    public JavaClass dao() {
        JavaClass dao = empty(CodeType.dao);
        dao.getImportList().add(CodeType.model.fillName(packageName, modelName));
        dao.getImportList().add("org.apache.ibatis.annotations.Mapper");
        dao.getImportList().add("wang.wangby.dao.BaseDao");
        return dao;
    }


    public JavaClass controller() {
        JavaClass controller = empty(CodeType.controller);
        controller.getImportList().add(Autowired.class.getName());
        controller.getImportList().add(RequestMapping.class.getName());
        controller.getImportList().add(RestController.class.getName());
        controller.getImportList().add(Remark.class.getName());
        controller.getImportList().add(Param.class.getName());
        controller.getImportList().add(Return.class.getName());
        controller.getImportList().add(Menu.class.getName());
        controller.getImportList().add(BaseController.class.getName());
        controller.getImportList().add(Pagination.class.getName());
        controller.getImportList().add(Response.class.getName());
        controller.getImportList().add(Message.class.getName());
        controller.getImportList().add(CodeType.model.fillName(packageName, modelName));
        controller.getImportList().add(CodeType.service.fillName(packageName, modelName));
        return controller;
    }

    public List<JavaField> getFields() {
        return model().getJavaFieldList();
    }

    //获得所有字段,并分组
    public List<List<JavaField>> getFieldRows(int size) {
        List<JavaField> fields = this.getFields();
        List result = new ArrayList();
        List current = new ArrayList();
        for (JavaField f : fields) {
            current.add(f);
            if (current.size() == size) {
                result.add(current);
                current = new ArrayList();
            }
        }
        if (current.size() != 0) {
            result.add(current);
        }
        return result;
    }

    public List<JavaClass> getAllClas() {
        List<JavaClass> classes = new ArrayList<>();
        classes.add(model());
        classes.add(controller());
        classes.add(service());
        classes.add(dao());
        return classes;
    }

    ;

    public CodeCreateResult getCodeCreateResult() {
        CodeCreateResult result = new CodeCreateResult();
        result.setJavaClasses(getAllClas());
        result.setPkName(getPkField().getName());
        result.setModelName(getModelName());
        result.setTableInfo(getTableInfo());
        return result;
    }
}
