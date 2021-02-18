package wang.wangby.tools.controller;

import wang.wangby.annotation.web.Menu;
import wang.wangby.web.controller.BaseController;

@Menu("工具箱")
abstract  public class ToolController extends BaseController {

    public int order(){
        return 100;
    }
}
