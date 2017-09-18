var cookieUtil = {

	// 获取指定名称的cookie的值
	getCookie : function(objName) {
		var arrStr = document.cookie.split("; ");
		for (var i = 0; i < arrStr.length; i++) {
			var temp = arrStr[i].split("=");
			if (temp[0] == objName)
				return unescape(temp[1]);
		}
	},
	// 添加cookie
	addCookie : function(objName, objValue, objHours) {
		var str = objName + "=" + escape(objValue);
		if (objHours > 0) { // 为时不设定过期时间，浏览器关闭时cookie自动消失
			var date = new Date();
			var ms = objHours * 3600 * 1000;
			date.setTime(date.getTime() + ms);
			str += "; expires=" + date.toGMTString();
		}
		document.cookie = str;
	},
	// 删除cookie
	delCookie : function(name) {
		var exp = new Date();
		exp.setTime(exp.getTime() - 1);
		var cval = cookier.getCookie(name);
		if (cval != null) {
			document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
		}
	}

};

/**
 * bootbox弹出框
 */
var Box = {
		alert : function(msg,callback,label){
			 var labText;
			 if(label){
				 labText=label
			 }else{
				 labText='关闭';
			 }
			 bootbox.alert({
			    buttons: {
				   ok: {
					    label: labText,
					    className: 'btn-sm btn-primary'
				    }
			    },
			    message: msg,
			    callback: function() {
			    	if(callback)
			    		callback();
			    },
			    title: '系统提示',
		    });
		},
		confirm : function(msg,callback){
			bootbox.confirm({
				title: "系统提示",
				message: msg, 
				callback:function(result){
					if(result){
						if(callback)
							callback();
					}
				},
				buttons: {
					"confirm" : {
							"label" : "确定",
							"className" : "btn-sm btn-danger"
						},
					"cancel" : {
							"label" : "取消",
							"className" : "btn-sm"}
				}
			});
		},
		dialog : function(msg,title,okback,canback){
			bootbox.dialog({  
                message: msg,  
                title: title,  
                buttons: {  
                    Cancel: {  
                        label: "取消",  
                        className: "btn-sm",  
                        callback: function () {
                        	canback();  
                        }  
                    }, 
                    OK: {  
                        label: "确定",  
                        className: "btn-primary",  
                        callback: function () {  
                        	okback();
                        }  
                    }  
                }  
            });  
        }
};

var App = {
	/**
	 * 提交进度
	 */
	bodyRefreshImg : function() {
		var content = [];
		content.push("<div id=\"append-loading\" style=\"position:absolute;top:0px; left:0px; width:100%; height:100%; filter: alpha(opacity=40); background-color: rgb(0, 0, 0);background-color: rgba(0, 0, 0, 0.4);z-index:20000\">");
		content
				.push("<div style=\"position:absolute; top:50%; left:50%; margin:-30px 0px 0px -70px; text-align:center;  border:1px solid #8DB2E3; width:120px; height:40px; font-size:8px;padding:10px; background:#fff; color:#15428B;\">");
		content.push("<span>提交中,请稍候...</span>");
		content.push("</div></div>");
		$('body').append(content.join(''));
	},
	/**
	 * Ajax提交表单
	 * 
	 * @param {}
	 *            url
	 * @param {}
	 *            callback
	 */
	formAjaxSubmit : function(formId, url, method, callback) {
		var timeout = setTimeout(this.bodyRefreshImg, 500);
		jQuery('#'+formId).ajaxSubmit({
			url : url,
			type : method,
			dataType : 'json',
			beforeSubmit : function() {
				var flag = jQuery('#'+formId).valid();
				if (!flag) {
					clearTimeout(timeout);
				}
				return flag;
			},
			success : function(result) {
				if(jQuery('#append-loading').length){
					jQuery('#append-loading').remove();
				}
				clearTimeout(timeout);
				callback(result);
			},
			error : function(result) {
				if(jQuery('#append-loading').length){
					jQuery('#append-loading').remove();
				}
				clearTimeout(timeout);
			}
		});
	},
	
	/**
	 * 普通提交表单
	 * 
	 * @param {}
	 *            url
	 * @param {}
	 *            callback
	 */
	formSubmit : function(formId, url, callback) {
		jQuery('#'+formId).form('submit', {
			url : url,
			onSubmit : function() {
				return jQuery(this).form('validate');
			},
			success : function(result) {
				var jsonData = $.parseJSON(result);
				if (jsonData.result) {
					if (callback)
						callback(jsonData);
				}
			}
		});
	},
	/**
	 * 普通提交
	 */
	ajaxSubmit : function(url, data, method, callback) {
		var timeout = setTimeout(this.bodyRefreshImg, 500);
		$.ajax({
			url : url,
			type : method,
			data : data,
			dataType: 'json',
			success : function(result) {
				if(jQuery('#append-loading').length){
					jQuery('#append-loading').remove();
				}
				clearTimeout(timeout);
				callback(result);
			},
			error : function(result) {
				if(jQuery('#append-loading').length){
					jQuery('#append-loading').remove();
				}
				clearTimeout(timeout);
			}
		});
	},
	/**
	 * 窗体对象
	 * 
	 * @param {}
	 *            config
	 */
	Win : function(config) {
		this.id = config.id ? config.id : 'win';
		this.frame_id = this.id + '_frame';
		this.title = config.title;
		this.url = config.url;
		this.width = config.width ? config.width : 400;
		this.height = config.height ? config.height : 300;

		this.open = function() {
			if ($('#' + this.id).length == 0)
				$('body').append('<div id="' + this.id + '"><iframe id="' + this.frame_id + '" style="width:100%;height:100%;" scrolling="no" frameborder="0" src=""/></div>');
			$('#' + this.frame_id)[0].src = this.url;
			$('#' + this.id).window({
				title : this.title,
				modal : true,
				minimizable : false,
				maximizable : false,
				collapsible : false,
				resizable : false,
				width : this.width,
				height : this.height
			});
			$('#' + this.id).window('open');
		};

		this.close = function() {
			$('#' + this.id).window('close');
		};
	},

	closeWin : function(winObj) {
		if (winObj) {
			winObj.window('close');
		} else if (parent) {
			parent.$('#win').window('close');
		} else {
			$('#win').window('close');
		}
	}

};

/**
 * 对Date的扩展，将 Date 转化为指定格式的String 月(M)、日(d)、12小时(h)、24小时(H)、分(m)、秒(s)、周(E)、季度(q)
 * 可以用 1-2 个占位符 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) eg: (new
 * Date()).format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 (new
 * Date()).format("yyyy-MM-dd E HH:mm:ss") ==> 2009-03-10 二 20:09:04 (new
 * Date()).format("yyyy-MM-dd EE hh:mm:ss") ==> 2009-03-10 周二 08:09:04 (new
 * Date()).format("yyyy-MM-dd EEE hh:mm:ss") ==> 2009-03-10 星期二 08:09:04 (new
 * Date()).format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18
 */
Date.prototype.format = function(fmt) {
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate(), // 日
		"h+" : this.getHours() % 12 == 0 ? 12 : this.getHours() % 12, // 小时
		"H+" : this.getHours(), // 小时
		"m+" : this.getMinutes(), // 分
		"s+" : this.getSeconds(), // 秒
		"q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
		"S" : this.getMilliseconds()
	// 毫秒
	};
	var week = {
		"0" : "/u65e5",
		"1" : "/u4e00",
		"2" : "/u4e8c",
		"3" : "/u4e09",
		"4" : "/u56db",
		"5" : "/u4e94",
		"6" : "/u516d"
	};
	if (/(y+)/.test(fmt)) {
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	}
	if (/(E+)/.test(fmt)) {
		fmt = fmt.replace(RegExp.$1, ((RegExp.$1.length > 1) ? (RegExp.$1.length > 2 ? "/u661f/u671f" : "/u5468") : "") + week[this.getDay() + ""]);
	}
	for ( var k in o) {
		if (new RegExp("(" + k + ")").test(fmt)) {
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
		}
	}
	return fmt;
};


/**
 * 在列表中显示日期
 * 
 * @param format
 * @returns
 */
function displayDate(dateValue, format) {
	if (typeof (dateValue) == 'undefined' || dateValue == '' || dateValue == null) {
		return '';
	}
	var date = new Date(dateValue);
	return date.format(!format ? "yyyy-MM-dd" : format);
}

// num表示要四舍五入的数,v表示要保留的小数位数。
function decimal(num, v) {
	var vv = Math.pow(10, v);
	return Math.round(num * vv) / vv;
}

jQuery.validator.addMethod("digitsOrOne",function(value, element){      
	if(value==''){
		return true;
	}
    return /^\d+(\.\d)?$/i.test(value);      
},"请填写正整数或精确到小数点后一位");  

jQuery.validator.addMethod("digitsOrTwo",function(value, element){      
	if(value==''){
		return true;
	}
    return /^\d+(\.\d{1,2})?$/i.test(value);      
},"请填写正整数或精确到小数点后二位");

var Validate = {
	remove : function(id){
		jQuery("#"+id).closest('.form-group').removeClass('has-error').addClass('has-info');
		jQuery("#"+id).rules("remove");
	},
	add : function(id,rule,msg){
		rule.messages = msg;
		jQuery("#"+id).rules("add", rule);  
	},
	init : function(formid,rules,messages,callback){
		jQuery("#"+formid).validate({
			errorElement: 'span',
			errorClass: 'help-block',
			focusInvalid: false,
			onkeyup: false,
		    ignore: "",
			rules: rules,
			messages: messages,
			invalidHandler: function (event, validator) {  
				$('.alert-danger', $('.login-form')).show();
			},
			highlight: function (e) {
				$(e).closest('.form-group').removeClass('has-info').addClass('has-error');
			},
			success: function (e) {
				$(e).closest('.form-group').removeClass('has-error').addClass('has-info');
				$(e).remove();
			},
			errorPlacement: function (error, element) {
				if(element.is(':checkbox') || element.is(':radio')) {
					var controls = element.closest('div[class*="col-"]');
					if(controls.find(':checkbox,:radio').length > 1) controls.append(error);
					else error.insertAfter(element.nextAll('.lbl:eq(0)').eq(0));
				}else if(element.is('.select2')) {
					error.insertAfter(element.siblings('[class*="select2-container"]:eq(0)'));
				}else if(element.is('.chosen-select')) {
					error.insertAfter(element.siblings('[class*="chosen-container"]:eq(0)'));
				}else error.insertAfter(element.parent());
			},
			submitHandler: callback
	  	});
	}
		
}