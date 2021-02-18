function AjaxUtil() {
}
// 获取html
AjaxUtil.prototype.html = function(url,param, callback) {
	var type="get";
	if(param){
		type="post";
	}
	$.ajax({
		type : type,
		url : url,
		data: param,
		error : function(request, textStatus) {
			var text=request.responseText;
			console.log(text);
			if(devModel){
				window.prompt("ajaxError:"+request.status+","+request.readyState+","+textStatus,url);
			}else{
				throw url+":"+textStatus;
			}
		},
		async: true,
		success : function(response){
			callback(response);
		}
	});
}

/**获取后台返回的json*/
AjaxUtil.prototype.json = function(url, data, fun) {
	var type = "get";
	if (data) {
		type = "post";
	}
	if (url.search('\\?') == -1) {
		url += "?";
	} else {
		url += "&";
	}
	url += "temp=" + new Date().getTime();
	jQuery.ajax({
		url : url, // 提交的页面
		data : data, // 从表单中获取数据
		type : type,
		beforeSend : function() {
		},
		error : function(request, textStatus) {
			var text=request.responseText;
			console.log(text)
			if(text){
				var js=eval("("+text+")");
				fun(js)
				return;
			}
			if(devModel){
				window.prompt("ajaxError:"+request.status+","+request.readyState+","+textStatus,url);
			}else{
				throw url+":"+textStatus;
			}
		},
		success : function(result) {
			fun(result);
		}
	});
}

//异步调用获取json,如果出错直接alert
AjaxUtil.prototype.data = function(url, data, callback) {
	dialog.loading()
	ajaxUtil.json(url,data,function (resp) {
        dialog.loading(false)
		if(!resp.success){
			dialog.alert(resp.message)
			return;
		}
        callback(resp.data);
    })
}

/**向后台发送json*/
AjaxUtil.prototype.post = function(url, data, fun) {
	var data=JSON.stringify(data);
	jQuery.ajax({
		url : url, 
		data : data, 
		type: "POST",
		contentType: 'application/json; charset=utf-8',
		datatype:"JSON",
		beforeSend : function() {
		},
		error : function(request, textStatus) {
			var text=request.responseText;
			console.log(text)
			if(text){
				var js=eval("("+text+")");
				fun(js)
				return;
			}
			if(devModel){
				window.prompt("ajaxError:"+request.status+","+request.readyState+","+textStatus,url);
			}else{
				throw url+":"+textStatus;
			}
		},
		success : function(result) {
			fun(result);
		}
	});
};

AjaxUtil.prototype.postJson = function(url, data, callback) {
    dialog.loading()
    ajaxUtil.post(url,data,function (resp) {
        dialog.loading(false)
        if(!resp.success){
            dialog.alert(resp.message)
            return;
        }
        callback(resp.data);
    })
};

var ajaxUtil = new AjaxUtil();