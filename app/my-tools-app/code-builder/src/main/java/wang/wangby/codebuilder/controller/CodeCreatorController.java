package wang.wangby.codebuilder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.web.Menu;
import wang.wangby.codebuilder.controller.vo.CodeCreateResult;
import wang.wangby.model.vo.JavaClass;
import wang.wangby.codebuilder.controller.vo.PageInfo;
import wang.wangby.codebuilder.utils.CodeCreator;
import wang.wangby.web.controller.BaseController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("codeCreator")
@Menu("代码生成器")
public class CodeCreatorController extends BaseController {

    @Menu("在线预览")
    @RequestMapping("index")
    public String index() {
        return $("index");
    }

    @Remark("通过sql生成java文件")
    @RequestMapping("createJava")
    public String createJava(String sql, String packageName) {
        CodeCreateResult result = new CodeCreateResult();
        CodeCreator codeCreator = new CodeCreator(sql, packageName);
        List<JavaClass> classes=new ArrayList<>();
        classes.add(codeCreator.model());
        classes.add(codeCreator.controller());
        classes.add(codeCreator.service());
        classes.add(codeCreator.dao());
        result.setJavaClasses(classes);
        result.setPkName(codeCreator.getPkField().getName());
        result.setModelName(codeCreator.getModelName());
        result.setTableInfo(codeCreator.getTableInfo());

        Map map=new HashMap();
        map.put("codeCreateResult",result);
        String model=templateUtil.parseTemplate("/codeCreator/templateModel.vm",map);
        result.model().setContent(model);

        String controller=templateUtil.parseTemplate("/codeCreator/templateController.vm",map);
        result.controller().setContent(controller);

        String service=templateUtil.parseTemplate("/codeCreator/templateService.vm",map);
        result.service().setContent(service);

        String dao=templateUtil.parseTemplate("/codeCreator/templateDao.vm",map);
        result.dao().setContent(dao);

        return $("createJava", result);
    }

    @Remark("通过sql生成页面文件")
    @RequestMapping("createPage")
    public String createPage(String sql, String packageName) {
        CodeCreator codeCreator = new CodeCreator(sql, packageName);

        Map map=new HashMap<>();
        map.put("codeCreator",codeCreator);
        List<PageInfo> pages=new ArrayList<>();

        //index
        String indexContent=templateUtil.parseTemplate("/codeCreator/templateIndex.vm",map);
        PageInfo page=new PageInfo();
        page.setName("index");
        page.setContent(indexContent);
        pages.add(page);

        //prepareInsert
        String prepareInsert=templateUtil.parseTemplate("/codeCreator/templatePrepareInsert.vm",map);
        PageInfo insertPage=new PageInfo();
        insertPage.setName("prepareInsert");
        insertPage.setContent(prepareInsert);
        pages.add(insertPage);

        //prepareUpdate
        String prepareUpdate=templateUtil.parseTemplate("/codeCreator/templatePrepareUpdate.vm",map);
        PageInfo updatePage=new PageInfo();
        updatePage.setName("prepareUpdate");
        updatePage.setContent(prepareUpdate);
        pages.add(updatePage);

        CodeCreateResult result = new CodeCreateResult();
        result.setPages(pages);
        return $("createPage", result);
    }
}