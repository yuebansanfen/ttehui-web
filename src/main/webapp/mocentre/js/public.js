$(document).ready(function(){
    //一些公用的函数
    window.fm = {};
    fm.UnBlock = function (msg, time) { // 弹出提示
        $.unblockUI();
        var msg  = msg || 'loading';
        var time = time || 2000;
        $.blockUI({
            message: '<h4>'+msg+'</h4>', 
            css: { 
                border: 'none', 
                padding: '15px', 
                backgroundColor: '#000', 
                '-webkit-border-radius': '10px', 
                '-moz-border-radius': '10px', 
                opacity: .5, 
                color: '#fff' 
            } 
        });
    }

    fm.showLoging = function (msg) { //显示加载中
        var msg  = msg || 'loading';
        $.blockUI({
            message: '<img id="displayBox" src="../../mocentre/images/loading.gif" width="100" height="100"/><br><h4>'+msg+'</h4>', 
            css: {
                border: 'none', 
                padding: '15px', 
                backgroundColor: '#000', 
                '-webkit-border-radius': '10px', 
                '-moz-border-radius': '10px', 
                opacity: .5, 
                color: '#fff' 
            }
        });
    }

    fm.closeUnBlock = function (times) { //关闭加载中
        if (times) {
            setTimeout(function () {
                $.unblockUI();
            }, times * 500);
        } else {
            $.unblockUI();
        }
    }
});

    