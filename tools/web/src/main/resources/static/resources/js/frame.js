function Frame() {
}

Frame.prototype.delete=function(pkName,url,successcCallback){
    successcCallback=successcCallback||tableUtil.remove;
    var selects =tableUtil.getSelections();
    if(selects.length==0){
        dialog.alert("请选择要删除的数据");
        return;
    }
    var count=selects.length;
    dialog.confirm('确定要删除这'+count+"条数据吗?",function(){
        var data='';
        var ids=[];
        for(var i=0;i<selects.length;i++){
            data+=pkName+"="+selects[i][pkName]+"&";
            ids.push(selects[i][pkName]);
        }
        ajaxUtil.json(url,data,function(resp){
            if(!resp.success){
                dialog.alert(resp.message);
            }else{
                successcCallback(pkName,ids);
            }
        })
    });
}

//显示修改页面
Frame.prototype.openUpdate=function (pkName,title,prepareUrl,updateUrl,size,successCallback) {
    size=size||2;
    successCallback=successCallback||tableUtil.update;
    ajaxUtil.html(prepareUrl,null,function(content){
        content="<form id='prepareUpdate'>"+content+"</form>";
        var vue=null;
        dialog.createPop('prepareUpdate',title,content,size,function (data) {
            var validate= vue.$validator.validateAll();
            validate.then((result)=>{
                if(result){
                    $('#pop'+'prepareUpdate').modal('hide');
                    var data=$("#prepareUpdate").serialize();
                    ajaxUtil.json(updateUrl,data,function(rest){
                        if(rest.success){
                            successCallback(rest.data[pkName],rest.data);
                        }else{
                            dialog.alert(rest.message);
                        }
                    });
                }
            })
            return false;
        });
        vue=vueUtil.init({
            el:"#prepareUpdate",
            data:updateData
        });
    });
}

Frame.prototype.update=function (pkName, title,prepareUrl,updateUrl,size) {
    var selects = tableUtil.getSelections();
    if(selects.length==0){
        dialog.alert("请选择要修改的数据");
        return;
    }
    if(selects.length!=1){
        dialog.alert("每次只能修改1条");
        return;
    }
    prepareUrl=prepareUrl+'?'+pkName+'='+selects[0][pkName];
    frame.openUpdate(pkName,title,prepareUrl,updateUrl,size);
}

Frame.prototype.prepareInsert=function (title, prepareUrl, insertUrl,successCallback,size,param) {
    size=size||2;
    successCallback=successCallback||tableUtil.insertRow;
    ajaxUtil.html(prepareUrl,param,function(content){
        content="<form id='prepareInsert'>"+content+"</form>";
        var vue=null;
        dialog.createPop('prepareInsert',title,content,size,function (data) {
            var validate= vue.$validator.validateAll();
            validate.then((result)=>{
                if(result){
                    $('#pop'+'prepareInsert').modal('hide');
                    var data=$("#prepareInsert").serialize();
                    ajaxUtil.json(insertUrl,data,function(rest){
                        if(rest.success){
                            successCallback(rest.data);
                        }else{
                            dialog.alert(rest.message);
                        }
                    });
                }
            })
            return false;
        });
         vue=vueUtil.init({
            el:"#prepareInsert",
            data:insertData
        });
    });
}

Frame.prototype.search=function (formId,url,vue) {
    var validate= vue.$validator.validateAll();
    validate.then((result)=>{
        if(result){
            tableUtil.loadByForm(formId,url);
        }
    })
}

var frame=new Frame();