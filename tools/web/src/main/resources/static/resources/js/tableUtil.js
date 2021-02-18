
function TableUtil() {
	this.sidePagination='';//客户端还是服务端分页
	//每次点击查询就保存一下查询条件,分页的时候要用到
	this.queryParam={};
	this.tableId="";//表格所在divid
}

//客端端分页
/**
 * @param divId 显示表格的div
 * @param columns 显示的列
 * @param idField 唯一标识的列名
 
 * */
TableUtil.prototype.client = function(divId,columns,updateConfig) {
	tableUtil.init(divId,columns,null,updateConfig);
}

TableUtil.prototype.defaultConfig=function() {
	return {
		  url: '',         //请求后台的URL（*）
		  method: 'get',                      //请求方式（*）
		  toolbar: '#toolbar',                //工具按钮用哪个容器
		  striped: true,                      //是否显示行间隔色
		  cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
		  sortable: true,                     //是否启用排序
		  sortOrder: "asc",                   //排序方式
		  queryParams: tableUtil.queryParams,//传递参数（*）
		  pagination: true,                   //是否显示分页（*）
		  sidePagination:'',           //分页方式：client客户端分页，server服务端分页（*）
		  pageNumber:1,                       //初始化加载第一页，默认第一页
		  pageSize: 10,                       //每页的记录行数（*）
		  pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
		  search: false,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
		  strictSearch: true,
		  showColumns: false,                  //是否显示所有的列
		  showRefresh: false,                  //是否显示刷新按钮
		  minimumCountColumns: 2,             //最少允许的列数
		  clickToSelect: true,                //是否启用点击选中行
		//  height: 500,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
		  uniqueId: null,                     //每一行的唯一标识，一般为主键列
		  showToggle:false,                    //是否显示详细视图和列表视图的切换按钮
		  cardView: false,                    //是否显示详细视图
		  detailView: false,                   //是否显示父子表
		  columns: [],
		  responseHandler:tableUtil.responseHandler,
		  onPostBody: function () {
	          return false;
	      }
	}
}


TableUtil.prototype.init = function(divId,columns,url,updateConfig) {
	this.queryParam={};
	this.tableId=divId;
	var config=tableUtil.defaultConfig();
	//如果传了url,客户端分页,否则服务端分页
	if(url){
		config.sidePagination='server';
	}else{
		config.sidePagination='clent';
	}
	config.url=url;
	config.columns=columns;
	tableUtil.sidePagination=config.sidePagination;
	if(updateConfig){
		updateConfig(config);
	}
	$('#'+divId).bootstrapTable(config);
	return config;
};


TableUtil.prototype.responseHandler=function(resp){
	var data={};
	if(this.sidePagination=='server'){
		if(resp.success){
			data["total"]=resp.data.totalCount;
			data["rows"]=resp.data.result;
		}else{
			dialog.alert(resp.message)
		}

	}
	return data;
}

TableUtil.prototype.queryParams=function(params){
	var pp=tableUtil.queryParam;
	pp["limit"]=params.limit;
	pp["offset"]=params.offset;
	pp["order"]=params.order;
	return pp;
}

TableUtil.prototype.load=function(url,param,callback){
	dialog.loading();
	ajaxUtil.json(url,param,function (resp) {
		dialog.loading(false);
		if(resp.success){
			var data=resp.data;
			if(tableUtil.sidePagination=='server'){
				data={"total":data.totalCount,"rows":data.result};
			}
			var tableId=tableUtil.tableId;
			$("#"+tableId).bootstrapTable('removeAll');
			//$('#'+tableId).bootstrapTable('selectPage',1);
			$('#'+tableId).bootstrapTable('load',data);
			if(callback){
				callback();
			}
		}else{
			dialog.alert(resp.message);
		}
    });
}

//通过form表单查询,form必须填写action
TableUtil.prototype.loadByForm=function(formId,url){
	var param=$("#"+formId).serializeArray();
	var data={};
	for(var i=0;i<param.length;i++){
		data[param[i].name]=param[i].value;
	}
	data.limit=10;
	data.offset=0;
	tableUtil.load(url,data);
}

TableUtil.prototype.insertRow=function(data){
	$("#"+tableUtil.tableId).bootstrapTable('insertRow', {index: 0, row: data});
}

//根据条件移除某些行,field字段名,values值数组
TableUtil.prototype.remove=function(field,values){
	$("#"+tableUtil.tableId).bootstrapTable('remove', {field: field, values: values});
}

TableUtil.prototype.update=function(id,row){
	$("#"+tableUtil.tableId).bootstrapTable('updateByUniqueId', {id: id, row: row});
}

TableUtil.prototype.getSelections=function(){
    return $("#"+tableUtil.tableId).bootstrapTable('getSelections');
}

//将某行变为超链接
TableUtil.prototype.link=function(linkId){
	linkId=linkId||detailLink;
	return function(value, row, index){
		return [
			'<a href="JavaScript:" id="'+linkId+'">'+value+'</a>'
		].join("")	
	}
}


TableUtil.prototype.dateToString=function(value, row, index){
    if(!value){
        return "";
    }
    value=eval(value);
    var d=new Date(value);
    return d.format("yyyy-MM-dd hh:mm:ss");
}

var tableUtil=new TableUtil();