const messages = {
    _default: (field) => `${field}的值无效`,
    after: (field, [target, inclusion]) => `${field}必须在${target}之后${inclusion ? '或等于' + target : ''}`,
    alpha: (field) => `${field}只能包含字母字符`,
    alpha_dash: (field) => `${field}能够包含字母数字字符、破折号和下划线`,
    alpha_num: (field) => `${field}只能包含字母数字字符`,
    alpha_spaces: (field) => `${field}只能包含字母字符和空格`,
    before: (field, [target, inclusion]) => `${field}必须在${target}之前${inclusion ? '或等于' + target : ''}`,
    between: (field, [min, max]) => `${field}必须在${min}与${max}之间`,
    confirmed: (field, [confirmedField]) => `${field}不能和${confirmedField}匹配`,
    credit_card: (field) => `${field}的格式错误`,
    date_between: (field, [min, max]) => `${field}必须在${min}和${max}之间`,
    date_format: (field, [format]) => `${field}必须符合${format}格式`,
    decimal: (field, [decimals = '*'] = []) => `${field}必须是数字，且能够保留${decimals === '*' ? '' : decimals}位小数`,
    digits: (field, [length]) => `${field}必须是数字，且精确到${length}位数`,
    dimensions: (field, [width, height]) => `${field}必须在${width}像素与${height}像素之间`,
    email: (field) => `${field}不是一个有效的邮箱`,
    excluded: (field) => `${field}不是一个有效值`,
    ext: (field) => `${field}不是一个有效的文件`,
    image: (field) => `${field}不是一张有效的图片`,
    included: (field) => `${field}不是一个有效值`,
    integer: (field) => `${field}必须是整数`,
    ip: (field) => `${field}不是一个有效的地址`,
    length: (field, [length, max]) => {
        if (max) {
            return `${field}长度必须在${length}到${max}之间`
        }

        return `${field}长度必须为${length}`
    },
    max: (field, [length]) => `${field}不能超过${length}个字符`,
    max_value: (field, [max]) => `${field}必须小于或等于${max}`,
    mimes: (field) => `${field}不是一个有效的文件类型`,
    min: (field, [length]) => `${field}必须至少有${length}个字符`,
    min_value: (field, [min]) => `${field}必须大于或等于${min}`,
    numeric: (field) => `${field}只能包含数字字符`,
    regex: (field) => `${field}格式无效`,
    required: (field) => `${field}不能为空`,
    size: (field, [size]) => `${field}必须小于${formatFileSize(size)}`,
    url: (field) => `${field}不是一个有效的url`,
    hostname: (field) => `格式为：xxx.xx.xx`,
};

const zh_CN = {
    name: 'zh_CN',
    messages,
};

VeeValidate.Validator.addLocale(zh_CN);
const config = {
    locale: 'zh_CN'
};

VeeValidate.Validator.extend('hostname', {
    getMessage: field => "请输入正确的主机名",
    validate: value => /^([\d|\w]+[\d|\w|.]*[\d|\w])$/.test(value)
});

VeeValidate.Validator.extend('integer', {
    getMessage: field => "请输入正确的主机名",
    validate: value => /^([\d]*)$/.test(value)
});

Vue.use(VeeValidate,config);

function VueUtil(){
    this.data={};//实际数据
}
VueUtil.prototype.init=function(vueConfig){
    if(!vueConfig.methods){
        vueConfig.methods={};
    }
    this.data=vueConfig.data;
    this.path=vueConfig.path;
    this.name=vueConfig.name;

    var $app=$(vueConfig.el);
    $app.find("input").each(function (idx,el) {
        vueUtil.initFormElement($(el));
    });
    $app.find("textarea").each(function (idx,el) {
        vueUtil.initFormElement($(el));
    });
    $app.find("select").each(function (idx,el) {
        vueUtil.initFormElement($(el));
    });

    var config=this.defaultConfig();
    if(!vueConfig.methods.insert){
        vueConfig.methods.insert=config.insert;
    }
    if(!vueConfig.methods.update){
        vueConfig.methods.update=config.update;
    }
    if(!vueConfig.methods.updateCallback){
        vueConfig.methods.updateCallback=config.updateCallback;
    }
    if(!vueConfig.methods.getCallback){
        vueConfig.methods.getCallback=config.getCallback;
    }
    if(!vueConfig.methods.prepareUpdate){
        vueConfig.methods.prepareUpdate=config.prepareUpdate;
    }
    if(!vueConfig.methods.prepareSubmit){
        vueConfig.methods.prepareSubmit=config.prepareSubmit;
    }

    if(!vueConfig.methods.del){
        vueConfig.methods.del=config.del;
    }
    return new Vue(vueConfig);
}

VueUtil.prototype.defaultConfig=function(){
    var javaData=this.data;
    var path=this.path;
    var name=this.name;
    return {
        insert:function () {
            if(!this.$options.methods.prepareSubmit()){
                return;
            }
            var postData={}
            postData[name]=javaData
            var url=path+"/insert";
            var validate= this.$validator.validateAll();
            validate.then((result)=>{
                if(result){
                    var callback=this.$options.methods.updateCallback;
                    ajaxUtil.postJson(url,postData,function (data) {
                        callback(data,"insert");
                    })
                }
            })
        },
        update:function(){
            if(!this.$options.methods.prepareSubmit()){
                return;
            }
            var postData={}
            postData[name]=javaData
            var url=path+"/update";
            var validate= this.$validator.validateAll();
            validate.then((result)=>{
                if(result){
                    var callback=this.$options.methods.updateCallback;
                    ajaxUtil.postJson(url,postData,function (data) {
                        callback(data,"update");
                    })
                }
            })
        },
        updateCallback:function (result,methodName) {
            dialog.pop("操作成功",methodName);
        },
        prepareUpdate:function (result,methodName) {
            var postData={}
            postData[name]=javaData
            var url=path+"/prepareUpdate.html";
            var callback=this.$options.methods.getCallback;
            ajaxUtil.post(url,postData,function (result) {
                callback(result);
            })
        },
        del:function () {
            var data = "id=" + this.id;
            var url=path+"/delete";
            var callback=this.$options.methods.updateCallback;
            ajaxUtil.data(url, data, function (data) {
                callback(data,"del");
            })
        },
        prepareSubmit:function () {
            return true;
        }
    }
}

VueUtil.prototype.submitHtml=function (vue,url,data,callback) {
    var validate= vue.$validator.validateAll();
    validate.then((result)=>{
        if(result){
            ajaxUtil.html(url,data,callback);
        }
    })
}

VueUtil.prototype.submit=function (vue,url,data,callback) {
    var validate= vue.$validator.validateAll();
    validate.then((result)=>{
        if(result){
            ajaxUtil.postJson(url,data,callback);
        }
    })
}

VueUtil.prototype.initFormElement=function ($el) {
    if($el.attr("type")=='hidden'){
        return;
    }
    if($el.attr("type")=='checkbox'){
        return;
    }
    var title= $el.attr("title");
    if(!title){
        //如果是td获取相邻的
        if($el.parent()[0].tagName=="TD"){
            title=$el.parent().prev().text().trim();
        }else{
            title=$el.parent().text().trim();
        }
    }
    //如果找不到title的值,就需要自己设置所有属性
    if(title){
        var errorSpan=$("<span v-show=\"errors.has('"+title+"')\" style=\"color: red\">{{ errors.first('"+title+"') }}</span>");
        $el.parent().append(errorSpan);
        $el.attr("data-vv-name",title);
        $el.attr("v-bind:style","errors.has('"+title+"') ? 'border-color:red' : ''");
        $el.attr("class","form-control");
        var name=$el.attr("name");
        if(name){
            $el.attr("v-model",name);
        }
    }
}

var vueUtil=new VueUtil();