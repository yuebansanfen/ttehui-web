 /* 验证名称是否重复 url为查询所有列表地址, tagName为Input的id */
function validateName(url, tagName) {
	var names = [];
	var oldName = $("#" + tagName).val();
	$.ajax({
		"async" : false,
		"type" : "POST",
		"url" : url,
		"success" : function(data) {
			var dataList = data.data;
			//循环每一条,判断每一条的key与你的Input ID相同，存放到names
			for ( var i in dataList) {
				var data = dataList[i];
				for ( var key in data) {
					if (key == tagName) {
						names.push(data[key]);
					}
				}
			}
		}
	});
	
	// 绑定变化事件
	$("#" + tagName).on('change', function() {
		for (var i = 0; i < names.length; i++) {
			if ($(this).val() == names[i]) {
				if ($(this).val()!=oldName) {
					$(this).val("");
					layer.msg('名称不可重复');
				}
			}
		}
	})
}