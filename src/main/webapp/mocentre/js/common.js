/**
 * 提供常用的js方法
 * Created by 王雪莹 on 2016/11/10.
 */

/**
 *  删除dataTables中被选中的行
 */
function del_checked(url,table){

    var ids = [];
    table.find('tr > td:first-child input:checkbox').each(function(){
        var chk = $(this).prop('checked');
        if(chk){
            ids.push($(this).attr("data-id"));
        }
    })
    if(ids.length>0){
        delList(ids,url);
    }
}

function delList(id,url){
    Box.confirm("是否删除选中的"+id.length+"条数据?",function(){
        jQuery.ajax({
            type: 'post',
            url:url,
            data: {"ids":JSON.stringify(id)},
            dataType: 'json',
            success: function(data){
                if(data.success){
                    mTable.fnDraw();
                    $.toast({
                        heading:'删除成功！',
                        text:'已成功删除'+id.length+'条数据。',
                        showHideTransition:'slide',
                        position:'bottom-right',
                        icon:'success'
                    })
                }else{
                    $.toast({
                        heading:'删除失败！',
                        text:'数据删除失败，请刷新重试',
                        showHideTransition:'slide',
                        position:'bottom-right',
                        icon:'warning'
                    })
                }
            },
            error: function(data) {
                Box.alert('系统异常，请联系系统管理员!');
            }
        });
    });
}

/**
 * 替換
 * @param s1
 * @param s2
 * @returns {string}
 */
String.prototype.replaceAll = function (s1, s2) {
    return this.replace(new RegExp(s1, "gm"), s2);
}

/**
 * 获取url上的地址
 * @param name
 * @returns {null}
 * @constructor
 */
function GetQueryString(name)
{
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null)return  unescape(r[2]); return null;
}

function uploadImg(o,url,type){
    var oldUrl ;
    var newUrl = [];
    if (!(o != undefined && type != undefined)) {
    } else {
        o.dmUploader({
            url: url,
            dataType: 'json',
            allowedTypes: 'image/*',
            extraData: {type: type},
            accept: {
                title: 'Images',
                extensions: 'gif,jpg,jpeg,bmp,png',
                mimeTypes: 'image/jpg,image/jpeg,image/png,image/gif,image/bmp'
            },
            onInit: function () {
                $.danidemo.addLog('#demo-debug', 'default', 'Plugin initialized correctly');
            },
            onBeforeUpload: function (id) {
                $('#upload_modal').modal({backdrop: 'static', keyboard: true});
                //$.danidemo.addLog('#demo-debug', 'default', 'Starting the upload of #' + id);
                $.danidemo.updateFileStatus(id, 'default', 'Uploading...');
            },
            onNewFile: function (id, file) {
                //var oldUrl = o.attr("url");
                //o.removeAttr("url");
                $('#demo-files').html('');
                $.danidemo.addFile('#demo-files', id, file);
                /*** Begins Image preview loader ***/
                if (typeof FileReader !== "undefined") {
                    var reader = new FileReader();
                    // Last image added
                    var img = $('#demo-files').find('.demo-image-preview').eq(0);
                    reader.onload = function (e) {
                        img.attr('src', e.target.result);
                    }
                    reader.readAsDataURL(file);
                } else {
                    // Hide/Remove all Images if FileReader isn't supported
                    $('#demo-files').find('.demo-image-preview').remove();
                }
                /*** Ends Image preview loader ***/
            },
            onComplete: function () {
                $.danidemo.addLog('#demo-debug', 'default', 'All pending tranfers completed');
                o.attr("url", newUrl);
            },
            onUploadProgress: function (id, percent) {
                var percentStr = percent + '%';
                $.danidemo.updateFileProgress(id, percentStr);
            },
            onUploadSuccess: function (id, data) {
                //$.danidemo.addLog('#demo-debug', 'success', 'Upload of file #' + id + ' completed');
                //$.danidemo.addLog('#demo-debug', 'info', 'Server Response for file #' + id + ': ' + JSON.stringify(data));
                var suc = data.success;
                var result = data.data;
                var opreviewPic = o.parent().siblings().find(".preview_pic");

                if (suc) {
                    $.danidemo.updateFileStatus(id, 'success', '上传完成');
                    $.danidemo.updateFileProgress(id, '100%');
                    setTimeout(function () {
                        $('#upload_modal').modal('hide')
                    }, 1500);
                    var h = Math.round((150 * result.height) / result.width);
                    opreviewPic.attr('src', result.url).attr('width', '150px').attr('height', h + 'px');
                    $('#img_logo').val(result.url);
                    newUrl.push(result.url);
                } else {
                    $.danidemo.updateFileStatus(id, 'success', '上传失败');
                    $('#upload_modal').modal('hide');
                    layer.alert(data.message, {title: '系统提示'});
                }
            },
            onUploadError: function (id, message) {
                $.danidemo.updateFileStatus(id, 'error', message);
                $.danidemo.addLog('#demo-debug', 'error', 'Failed to Upload file #' + id + ': ' + message);
            },
            onFileTypeError: function (file) {
                $.danidemo.addLog('#demo-debug', 'error', 'File \'' + file.name + '\' cannot be added: must be an image');
            },
            onFileSizeError: function (file) {
                $.danidemo.addLog('#demo-debug', 'error', 'File \'' + file.name + '\' cannot be added: size excess limit');
            },
            onFallbackMode: function (message) {
                $.danidemo.addLog('#demo-debug', 'info', 'Browser not supported(do something else here!): ' + message);
            }
        });
    }
}


