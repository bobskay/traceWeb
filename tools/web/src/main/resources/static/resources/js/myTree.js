function MyTree() {

}

MyTree.prototype.setting = function () {
    var nodeClick=this.zTreeOnClick;
    return {
        callback: {
            onClick: nodeClick
        }
    }
};

//单击节点
MyTree.prototype.zTreeOnClick = function (event, treeId, treeNode) {
    dialog.alert("点击了节点:"+treeNode.id);
}

/**
 * 加载树
 * @param url 数据url
 * @param param 参数
 * @param ztree 显示位置,默认显示在class=ztree的元素内
 * */
MyTree.prototype.load = function (url, param,ztree) {
    ztree=ztree||$(".ztree");
    this.url=url;
    this.param=param;
    this.ztree=ztree;
    var setting=this.setting();
    var tree=this;
    ajaxUtil.data(url, param, function (data) {
        zTreeObj = $.fn.zTree.init(ztree, setting, data);
        zTreeObj.expandAll(true);
        var height=document.documentElement.clientHeight-5;
        var top=$("#zkTree").offset().top;
        ztree.css("height", height-top);
        ztree.css("width", "100%");
        ztree.css("background-color", "white");
        ztree.css("margin", "2px");
    })
}

//更新图标
MyTree.prototype.updateIcon = function (node) {
    if(node.leaf){
        node.icon="/resources/img/page.png";
    }else{
        node.iconOpen="/resources/img/open.png";
        node.iconClose="/resources/img/close.png";
    }
    var children=node.children;
    if(!children||children.length==0){
        return;
    }
    for(var i=0;i<children.length;i++){
        this.updateNode(children[i]);
    };
}

//重新加载
MyTree.prototype.reload = function () {
    this.load(this.url,this.param,this.ztree);
}
