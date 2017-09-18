function validate(o) {
	o.next(".error").remove();
	var types = $(eval('(' + o.attr('valid') + ')'));
	var max = o.attr('max');
	var min = o.attr('min')

	if (min == undefined) {
		min = 0;
	}
	var value = o.val();
	var msg = 'success';
	var msg1 = 'success';
	// 获取第一个验证失败的msg
	for ( var i in types) {
		msg = run(o, types[i]);
		if (msg != "success") {
			break;
		}
	}
	// 如果有一个验证失败
	if (msg != "success") {
		var ospan = $("<span style='color:#fc4343' class='error'>" + msg
				+ "</span>");

		o.val("").after(ospan);
	} else {
		o.next(".error").remove();
		// 获取文本框输入的值
		msg1 = run1(max, min, value);
		if (msg1 != "success") {
			var ospan1 = $("<span style='color:#fc4343' class='error'></br>"
					+ msg1 + "</span>");
			o.val("").after(ospan1);
		} 
	}
}
// 长度验证
function run1(max, min, value) {
	var len = 0;
	for (var i = 0; i < value.length; i++) {
		var length = value.charCodeAt(i);
		if (length >= 0 && length <= 128) {
			len += 1;
		} else {
			len += 2;
		}
	}
	if (max == undefined) {
		if (len < min) {
			return '英文符号必须大于' + min;
		}
	}
	if (len < min || len > max) {
		return '英文符号必须在' + min + '~' + max + '之间，中文字符必须在' + Math.ceil(min / 2) + '~'
				+ Math.floor(max / 2) + '之间';
	}
	return 'success';
}
// 运行验证
function run(o, type) {
	var pattern = null;
	var val = o.val();
	switch (type) {
	case 'null':
		// 为空验证
		if ($.trim(val).length == 0) {
			msg = '不能为空！';
			return msg;
		}
		return 'success';
	case 'numeric':
		// 数值验证
		pattern = /^[-+]?[0-9]+(\.[0-9]+)?$/;
		//去除逗号后再做验证
		if (val !="" &&  !pattern.test(val.replace(/,/g,""))) {
			msg = '你输入的不是数值，请输入数值！';
			return msg;
		}

		return 'success';
	case 'notchinese':
		// 不能包含中文验证
		pattern = /^[a-zA-Z_\d-]+$/;
		if (!pattern.test($.trim(val))) {
			msg = ' 请不要输入中文！';
			return msg;
		}
		return 'success';
	case 'email':
		// 邮箱验证
		pattern = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
		if (!pattern.test(val)) {
			msg = ' 请输入正确的邮箱格式！';
			return msg;
		}
		return 'success';
	case 'mobile':
        //移动电话验证
        pattern = /^((\(\d{2,3}\))|(\d{3}\-))?((13\d{9})|(15\d{9})|(18\d{9}))$/;
        if (!pattern.test(val)) {
			msg = ' 请输入正确的手机号手机格式！';
			return msg;
		}
		return 'success';
	case 'phone':
        //固定电话验证
        pattern = /^((\(\d{2,3}\))|(\d{3}\-))?(\(0\d{2,3}\)|0\d{2,3}-)?[1-9]\d{6,7}(\-\d{1,4})?$/;
        if (!pattern.test(val)) {
			msg = ' 请输入正确的手机号手机格式！';
			return msg;
		}
		return 'success';
	case 'password':
        //匹配密码，以字母开头，长度在6-12之间，只能包含字符、数字和下划线
        pattern = /^http:\/\/[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\’:+!]*([^<>\"])*$/;
		if (!pattern.test(val)) {
			msg = ' 请输入正确的密码，以字母开头，长度在6-12之间，只能包含字符、数字和下划线！';
			return msg;
		}
		return 'success';
	case 'positiveInteger':
		pattern = /^[0-9]*[1-9][0-9]*$/;
		//去除逗号后再做验证
		if (!pattern.test(val.replace(/,/g,""))) {
			msg = '请输入正整数';
			return msg;
		}
		return 'success';
	case 'IP':
			// ip
			pattern = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)($|(?!\.$)\.)){4}$/
			if (!pattern.test(val)) {
				msg = '请输入正确的ip地址';
				return msg;
			}
			return 'success';
	}
	return 'success';
}

