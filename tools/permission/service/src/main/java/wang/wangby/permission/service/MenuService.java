package wang.wangby.permission.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wang.wangby.annotation.Remark;
import wang.wangby.permission.dao.MenuDao;
import wang.wangby.permission.entity.Menu;
import wang.wangby.repostory.service.BaseService;
import wang.wangby.utils.CollectionUtil;
import wang.wangby.utils.tree.TreeUtil;
import wang.wangby.web.MenuProvider;
import wang.wangby.web.dto.MenuInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MenuService extends BaseService<Menu> {
    @Autowired
    MenuDao menuDao;
    @Autowired
    MenuProvider menuProvider;

    private volatile  List<Menu> menuCache=null;

    public Menu newModel() {
        return new Menu();
    }

    public Menu getMenuByCode(String menuCode) {
        Menu menu=new Menu();
        menu.setMenuCode(menuCode);
        return this.unique(menu);
    }

    public void updateMenu(Menu menu){
        super.update(menu);
        menuCache=null;
    }



    @Transactional
    @Remark("比较数据库和系统的菜单,发现有不一样的就新增")
    public List<Menu> getAllMenu() {
        if(menuCache!=null){
            return menuCache;
        }
        synchronized (this){
            if(menuCache!=null){
                return menuCache;
            }
            List<MenuInfo> mappings=menuProvider.getMappings();
            List<Menu> db=getAll();
            List<Menu> add=new ArrayList<>();
            Map<String, Menu> menuMap= CollectionUtil.toMap(db, Menu::getMenuCode);
            TreeUtil.iteratorIndex(mappings,(mm, idx)->{
                if(menuMap.containsKey(mm.getId())){
                    return true;
                }
                Menu m = Menu.createByController(mm, idx);
                m.setMenuId(newId());
                log.info("发现新菜单:"+m);
                add.add(m);
                return true;
            });
            if(add.size()>0){
                insertBatch(add);
                db.addAll(add);
            }
            menuCache=db;
            return db;
        }
    }
}

