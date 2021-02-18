function Dialog(){
	this.loandingImg="<img src='"+staticRoot+"/img/loading.gif'>";
	this.zindex=99999;//为防止后弹出来的覆盖新的,每弹一次zindex就加1
}

/**
 * 弹出处理中对话框
 * @param show 显示或隐藏
 * */
Dialog.prototype.loading = function(show) {
	var id="loadingDialog";
	if(show==false){
		$("#pop"+id).modal('hide');
		return;
	}
	
	var img="<center>"+this.loandingImg+"</center>";
	this.createPop(id,"处理中,请稍后...",img,0);
}


//弹出确定框
Dialog.prototype.alert = function(content,title) {
	if(!content){
		this.createPop('alertDialog',"content is null",null,1,null)
		return;
	}
	title=title||'';
	if(content.length>100){
        this.createPop('alertDialogLarge',title,content,2,null)
	}else if(content.length>10){
		if(title){
			this.createPop('alertDialog',title,content,1,null)
		}else{
			this.createPop('alertDialog',content,null,1,null)
		}
	}else{
		if(title){
			this.createPop('alertDialogMiddle',title,content,0,null)
		}else{
			this.createPop('alertDialogMiddle',content,null,0,null)
		}
	}
	
}

//弹出确认框
Dialog.prototype.confirm = function(content,callback) {
	this.createPop('confirmDialog',content,null,0,callback)
}

//弹出内容
Dialog.prototype.pop = function(title,content,callback) {
	var id=this.toId(title);
	var size=0;
    if(content.length>500){
        size=3;
    }else if(content.length>100){
        size=2
    }else if(content.length>10){
    	size=1;
    }
	this.createPop(id,title,content,size,callback)
}

//将一段字符串转为可以当id用的字符
Dialog.prototype.toId = function(text) {
    var reg = new RegExp( /[^0-9a-zA-Z\u4e00-\u9fa5]/ , "g" )
    return text.replace(reg,'');
}

//弹出页面
Dialog.prototype.html = function(title,url,param,callback) {
	var id=this.toId(title);
	ajaxUtil.html(url,param,function(content){
		dialog.createPop(id,title,content,2,callback);
	});
}

Dialog.prototype.hide = function(title) {
	var id=this.toId(title);
	$('#pop'+id).modal('hide');
}

//创建form表单
Dialog.prototype.form = function(formId,title,url,callback,size) {
	size=size||2;
	ajaxUtil.html(url,null,function(content){
		content="<form id='"+formId+"' name='"+formId+"'>"+content+"</form>";
		dialog.createPop(formId,title,content,size,callback);
	});
}

Dialog.prototype.systemDialog=function(id){
	if(id=='alertDialog'||id=="confirmDialog"||id=="loadingDialog"||id=="alertDialogMiddle"){
		return true;
	}
	return false;
}

/**
 * 弹出一个dialog,如果对话框不存在,就创建一个
 * @param id 对话框唯一标识
 * @param title 标题
 * @param content 显示内容
 * @param callback 点击确认后的回调方法,如果为空,确定按钮就改为关闭,该方法执行返回false不关闭对话框,否则关闭
 * @param size 0,1,2三种尺寸
 * */
Dialog.prototype.createPop = function(id,title,content,size,callback) {
	//所有弹出窗的内容都放在_popDiv下,每次创建窗口的时候都会将原有内容清空
	//所以只能弹出一个窗口,系统窗口除外,如alter,confirm,loading
	if(!dialog.systemDialog(id)){
		if(!$("#_popDiv").html()){
			var popDiv="<div id='_popDiv'></div>";
			$(document.body).append(popDiv);
		}else{
			$("#_popDiv").html("");
		}
	}
	
	
	this.zindex=this.zindex+1;
	var oldTitle=$("#title"+id).html();
	//如果存在说明已经创建过一次
	if(oldTitle){
		$("#title"+id).html(title);
		$("#content"+id).html(content);
		$("#okBtn"+id).unbind('click').click(function(){
			if(callback($('#pop'+id))!=false){
				$('#pop'+id).modal('hide');
			}
		});
		$('#pop'+id).modal();
		$("#"+id).attr("style","z-Index:"+this.zindex+"!important");
		//确定按钮获得焦点
		$("#cancelBtn"+id).focus();
		return;
	}
		
	var html=this.createHtml(id,title,content,size,callback);
	if(dialog.systemDialog(id)){
		$(document.body).append(html);
	}else{
		$("#_popDiv").append(html);
	}
	$("#cancelBtn"+id).unbind('click').click(function(){
		$('#pop'+id).modal('hide');
	});
	$("#okBtn"+id).unbind('click').click(function(){
		if(callback($('#pop'+id))!=false){
			$('#pop'+id).modal('hide');
		}
	});
	$('#pop'+id).modal();
	$("#"+id).attr("style","z-Index:"+this.zindex+"!important");
	//确定按钮获得焦点
	$("#cancelBtn"+id).focus();
	
}


/**
 * 创建对话框的html
 * @param id 对话框唯一标识
 * @param title 标题
 * @param content 对话框内容,可以为空
 * @param size 尺寸0小,1普通,2中断,3大,默认是1
 * @param callback 点击确认后的回调方法,如果为空,就不回调
 * */
Dialog.prototype.createHtml=function(id,title,content,size,callback){
	var modalSize="";
	var width="";
	if(size==0){
		modalSize="modal-sm";
	}else if(size==2){
		modalSize="modal-lg";
	}else if(size==3){
        modalSize="modal-lg";
        width="width: 80%";
    }
	var html='<div id="pop'+id+'" class="modal" tabindex="-1">\
	<div class="modal-dialog '+modalSize+'" style="padding-top: 200px;'+width+'">\
	<div class="modal-content">\
		<div class="modal-header">\
			<button type="button" class="close" data-dismiss="modal">&times;</button>\
			<h4 class="blue bigger" id="title'+id+'">'+title+'</h4>\
		</div>';
	if(content){
		html+='<div class="modal-body"id="content'+id+'">'+content+'</div>';
	}
	if(!callback){
		html+='\
			<div class="modal-footer">\
				<button class="btn btn-sm btn-primary" id="cancelBtn'+id+'">\
					<i class="ace-icon fa fa-check" ></i> 确定\
				</button>\
			</div>\
		  </div>\
         </div>\
	 </div>';
	}else{
		html+='\
			<div class="modal-footer">\
				<button class="btn btn-sm btn-default" id="cancelBtn'+id+'">\
					<i class="ace-icon fa fa-times"></i> 取消\
				</button>\
				<button class="btn btn-sm btn-primary" id="okBtn'+id+'">\
					<i class="ace-icon fa fa-check"></i> 确定\
				</button>\
			</div>\
		  </div>\
         </div>\
	 </div>';
	}
	return html;
}


var dialog=new Dialog();