devModel=true;
//g全局,m多行
String.prototype.replaceAll = function (from, to) {
    return this.replace(new RegExp(from, "gm"), to);
}

String.prototype.toDateTime = function () {
    try{
        var d=new Date(this);
        return d.format('yyyy-MM-dd hh:mm:ss');
    }catch (e) {
        return "无法将"+this+"转为时间:"+e;
    }
}

String.prototype.fileSize = function () {
    try{
        var num=Number(this);
        return num.fileSize();
    }catch (e) {
        return "无法将"+this+"转为文件大小:"+e;
    }
}

Number.prototype.toDateTime = function () {
    var d=new Date(this)
    return d.format('yyyy-MM-dd hh:mm:ss');
}

Number.prototype.fileSize = function () {
    if(this<1024){
        return this+"B";
    }
    var num=this/1024;
    if(num<1024){
        return num.toFixed(2)+"K";
    }
    num=num/1024;
    if(num<1024){
        return num.toFixed(2)+"M";
    }
    num=num/1024;
    if(num<1024){
        return num.toFixed(2)+"G";
    }
}


//日期格式
//date.format("yyyy-MM-dd hh:mm:ss");
Date.prototype.format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1,                 //月份   
        "d+": this.getDate(),                    //日
        "h+": this.getHours(),                   //小时   
        "m+": this.getMinutes(),                 //分   
        "s+": this.getSeconds(),                 //秒   
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度   
        "S": this.getMilliseconds()             //毫秒   
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
};

//工具类,提供在一些在框架里常用的方法
function Utils() {
}

//获取地址栏search里的参数
Utils.prototype.getParam = function (name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); // 构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg); // 匹配目标参数
    if (r != null)
        return unescape(r[2]);
    return null; // 返回参数值
}
Utils.prototype.getUrl=function () {
    var url=window.location.href+"";//http://127.0.0.1:8080/abc?forward=/filePush/index
    var start=url.indexOf("//");
    url=url.substring(start+2);//127.0.0.1:8080/abc?forward=/filePush/index
    var begin=url.indexOf("/");
    var end=url.indexOf("?");
    if(end==-1){
        return url.substring(begin);///abc
    }else{
        return url.substring(begin,end);
    }
}



Utils.prototype.jsonToStr = function (json) {
    if (typeof json != 'string') {
        json = JSON.stringify(json, undefined, 2);
    }
    json = json.replace(/&/g, '&').replace(/</g, '<').replace(/>/g, '>');
    return json
        .replace(
            /("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g,
            function (match) {
                var cls = 'number';
                if (/^"/.test(match)) {
                    if (/:$/.test(match)) {
                        cls = 'key';
                    } else {
                        cls = 'string';
                    }
                } else if (/true|false/.test(match)) {
                    cls = 'boolean';
                } else if (/null/.test(match)) {
                    cls = 'null';
                }
                return '<span class="' + cls + '">' + match + '</span>';
            });
}

//创建一个select,list为[{key:'',text:''},{key:'',text:''}]
Utils.prototype.createSelect=function(divId,data,callbackMethodName){
    if(!data||!data.length){
        $("#"+divId).html("data is empty");
        return;
    }
    var name=$("#"+divId).attr('name');
    var validate=$("#"+divId).attr('validate');
    var title=$("#"+divId).attr('title');
    var inputId=divId+'input';
    var spanId=divId+'span';
    var ulId=divId+"ul";
    var validate=validate||'';
    var html='\
	  <button type="button"  class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">\
		<input type="hidden" id="'+inputId+'" name="'+name+'" validate="'+validate+'" title="'+title+'" value=""/>\
			<span id="'+spanId+'"></span> <span class="caret" ></span>\
	 </button>\
	 <ul id="'+ulId+'" class="dropdown-menu"></ul>';
    $("#"+divId).html(html);
    utils.updateSelect(divId,data,callbackMethodName);
    utils.setSelectValue(divId,data[0].key,data[0].text,callbackMethodName);
}

Utils.prototype.updateSelect=function(divId,data,callbackMethodName){
    var ulId=divId+"ul";
    var  html='';
    callbackMethodName=callbackMethodName||'';
    for(var i=0;i<data.length;i++){
        html+="<li><a href=\"javascript:\" onclick=\"utils.setSelectValue('"+divId+"','"+data[i].key+"','"+data[i].text+"','"+callbackMethodName+"')\">"+data[i].text+"</a></li>";
    }
    $("#"+ulId).html(html);
}

Utils.prototype.setSelectValue=function(divId,key,text,callback){
    var inputId=divId+'input';
    var spanId=divId+'span';
    $("#"+inputId).val(key);
    $("#"+spanId).text(text);
    if(callback){
        callback=eval(callback);
        callback(key,text)
    }

}


var utils=new Utils();
