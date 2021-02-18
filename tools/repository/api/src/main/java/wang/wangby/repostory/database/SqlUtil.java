package wang.wangby.repostory.database;

import lombok.extern.slf4j.Slf4j;
import wang.wangby.exception.Message;
import wang.wangby.repostory.database.dto.ColumnInfo;
import wang.wangby.repostory.database.dto.TableInfo;
import wang.wangby.utils.StringPicker;
import wang.wangby.utils.StringUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SqlUtil {

    public static TableInfo toTable(String createSql) {
        //去掉头尾空格,将连续的空白换成一个空格
        String formatted = createSql.trim().replaceAll(StringUtil.REG_BLANK, " ");
        formatted = formatted.replaceAll("(?i)comment", "comment");
        formatted = formatted.replaceAll("(?i)create", "create");
        formatted = formatted.replaceAll("(?i)table", "table");
        formatted = formatted.replace("`", "");
        if (formatted.indexOf("create table") == -1) {
            throw new Message("sql格式不正确,找不到建表语句:" + formatted);
        }
        log.debug("准备解析sql:" + formatted);
        StringPicker picker = new StringPicker(formatted);
        picker.next("create table");
        TableInfo tableInfo = new TableInfo();
        List<ColumnInfo> cols = new ArrayList<>();
        tableInfo.setColumns(cols);
        String tableName = picker.next("(").trim();
        tableInfo.setTableName(tableName.trim());
        while (true) {
            ColumnInfo columnInfo = pickColumn(picker, tableInfo);
            if (columnInfo == null) {
                break;
            }
            cols.add(columnInfo);
        }
        return tableInfo;
    }

    //logTaskId            bigint not null comment '主键',
    private static ColumnInfo pickColumn(StringPicker picker, TableInfo tableInfo) {
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setIsPk(false);
        String name = picker.next(" ");
        if (name == null) {
            return null;
        }
        if (StringUtil.isEmpty(name.trim())) {
            name = picker.next(" ");
        }

        if ("primary".equalsIgnoreCase(name)) {
            String pk = picker.next("(", ")", false);
            for (ColumnInfo col : tableInfo.getColumns()) {
                if (col.getColumnName().equalsIgnoreCase(pk)) {
                    col.setIsPk(true);
                    return null;
                }
            }
            throw new RuntimeException("找不到主键:" + pk);
        }

        //log.debug("发现字段:"+name);
        if (StringUtil.isEmpty(name)) {
            return null;
        }
        columnInfo.setColumnName(name);
        setDataType(picker, columnInfo);
        setComment(picker, columnInfo);

        return columnInfo;

    }

    private static void setComment(StringPicker picker, ColumnInfo columnInfo) {
        //not null
        String nullable = "";
        if (picker.isBefore("comment", ",")) {
            //comment位置比,小,但comment==-1,说明到了最后1个字段
            if (picker.indexOf("comment") == -1) {
                columnInfo.setNullable(true);
                picker.next(",");
                return;
            }
            nullable = picker.next("comment");
            String comment = picker.next("'", "'", false);
            columnInfo.setColumnComment(comment);

            //如果)先出现说明字段解析完毕
            if (picker.isBefore(",", ")")) {
                picker.next(",");
            } else {
                picker.next(")");
            }
        } else {
            nullable = picker.next(",");
        }
        if ("not null".equalsIgnoreCase(nullable)) {
            columnInfo.setNullable(false);
        } else {
            columnInfo.setNullable(true);
        }
    }

    private static void setDataType(StringPicker picker, ColumnInfo columnInfo) {
        //如果括号在空格前说明数据类型有长度
        String dataType = "";
        if (picker.isBefore("(", " ") && picker.indexOf("(") != -1) {
            dataType = picker.next("(");
            String length = picker.next(")");
            if (!StringUtil.isInteger(length)) {
                String error = MessageFormat.format("字段长度不正确:column={0},length={1}", columnInfo.getColumnName(), length);
                throw new Message(error);
            }
            columnInfo.setMaxLength(Integer.parseInt(length));
        } else {
            dataType = picker.next(" ");
        }
        columnInfo.setDataType(dataType);
    }


}
