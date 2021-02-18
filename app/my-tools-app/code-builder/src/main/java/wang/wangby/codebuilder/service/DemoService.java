package wang.wangby.codebuilder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.wangby.codebuilder.dao.DemoDao;
import wang.wangby.codebuilder.model.DemoModel;
import wang.wangby.entity.Pagination;
import wang.wangby.repostory.database.dto.ColumnInfo;
import wang.wangby.utils.StrBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class DemoService {

    @Autowired
    DemoDao demoDao;

    public Pagination selectPage(DemoModel query, Integer offset, Integer limit) {
        query.put("tableName", query.tableInfo().getTableName());
        query.put("condition", getCondition(query));
        query.put("columns", getColumns(query));

        long count = demoDao.getCount(query);
        if (offset == null || offset > count) {
            return new Pagination(count, new ArrayList(), offset, limit);
        }

        if (offset == null) {
            offset = 0;
        }
        if (limit == null) {
            limit = Pagination.DEFAULT_SIZE;
        }
        query.put("limit", "limit " + offset + "," + limit);
        List list = demoDao.select(query);
        return new Pagination(count, list, offset, limit);
    }


    //生成查询条件
    private String getCondition(DemoModel query) {
        return null;
    }

    //生成查询条件
    private String getColumns(DemoModel demoModel) {
        StrBuilder sb = new StrBuilder();
        for (ColumnInfo column : demoModel.tableInfo().getColumns()) {
            sb.append(column.getColumnName() + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }


}
